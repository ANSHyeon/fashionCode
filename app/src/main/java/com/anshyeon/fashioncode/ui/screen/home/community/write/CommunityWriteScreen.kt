package com.anshyeon.fashioncode.ui.screen.home.community.write

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.anshyeon.fashioncode.R
import com.anshyeon.fashioncode.ui.component.appBar.BackButtonAppBar
import com.anshyeon.fashioncode.ui.component.button.RectangleButton
import com.anshyeon.fashioncode.ui.theme.Gray
import kotlinx.coroutines.launch

@Composable
fun CommunityWriteScreen(navController: NavHostController) {

    val viewModel: CommunityWriteViewModel = hiltViewModel()
    val scrollState = rememberScrollState()

    val selectedImageList by viewModel.selectedImageList.collectAsStateWithLifecycle()
    val postTitle by viewModel.postTitle.collectAsStateWithLifecycle()
    val postBody by viewModel.postBody.collectAsStateWithLifecycle()

    val isSubmitAble = postTitle.isNotEmpty() && postBody.isNotEmpty()
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris ->
            if (selectedImageList.size + uris.size <= 10) {
                viewModel.addImageUris(uris)
            }
        }
    )

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 48.dp)
                .verticalScroll(scrollState)
        ) {
            BackButtonAppBar(title = stringResource(id = R.string.label_app_bar_community_write)) {
                viewModel.navigateBack(navController)
            }
            ImageSelector(selectedImageList, multiplePhotoPickerLauncher) { index ->
                viewModel.removeImageUris(index)
            }
            Spacer(modifier = Modifier.height(20.dp))
            TitleTextField(postTitle) { newTitle ->
                viewModel.changeTitle(newTitle)
            }
            BodyTextField(postBody, scrollState) { newBody ->
                viewModel.changeBody(newBody)
            }
        }
        RectangleButton(
            text = stringResource(id = R.string.label_submit),
            visibility = isSubmitAble
        ) {
            viewModel.submitPost(navController)
        }
    }
}

@Composable
private fun ImageSelector(
    selectedImageList: List<Uri>,
    multiplePhotoPickerLauncher: ManagedActivityResultLauncher<String, List<@JvmSuppressWildcards Uri>>,
    onclick: (index: Int) -> Unit
) {
    Row {
        GalleryImageSelector(selectedImageList.size) {
            multiplePhotoPickerLauncher.launch("image/*")
        }
        Spacer(modifier = Modifier.size(10.dp))
        LazyRow {
            itemsIndexed(selectedImageList) { index, imageUri ->
                SelectedImage(imageUri) {
                    onclick(index)
                }
            }
        }
    }
}

@Composable
private fun GalleryImageSelector(selectedImageCount: Int, onclick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(start = 24.dp, top = 27.dp)
            .clickable {
                onclick()
            }
    ) {
        Canvas(
            modifier = Modifier
                .size(64.dp)
        ) {
            drawRoundRect(
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                color = Gray,
            )
        }
        Icon(
            modifier = Modifier
                .padding(top = 10.dp)
                .width(64.dp),
            painter = painterResource(id = R.drawable.ic_camera),
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .padding(top = 40.dp)
                .width(64.dp),
            text = "$selectedImageCount/10",
            style = TextStyle(
                fontSize = 14.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SelectedImage(imageUri: Uri, onclick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 80.dp, height = 90.dp)
            .padding(start = 6.dp, top = 16.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .size(64.dp)
                .align(Alignment.BottomStart),
            model = imageUri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_launcher_background)
        )
        IconButton(
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.TopEnd),
            onClick = { onclick() }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun TitleTextField(
    text: String,
    onTextChanged: (String) -> Unit,
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = text,
        onValueChange = onTextChanged,
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        ),
        colors = TextFieldDefaults.textFieldColors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            backgroundColor = Color.White,
            textColor = Color.Black
        ),
        placeholder = {
            Text(
                text = stringResource(id = R.string.hint_post_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Gray,
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
    )
}

@Composable
private fun BodyTextField(
    text: String,
    scrollState: ScrollState,
    onTextChanged: (String) -> Unit,
) {
    var prevHeight by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    TextField(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged {
                val diff = it.height - prevHeight
                prevHeight = it.height
                if (prevHeight == 0 || diff == 0) {
                    return@onSizeChanged
                }

                coroutineScope.launch {
                    scrollState.animateScrollTo(
                        scrollState.value + diff
                    )
                }
            },
        value = text,
        onValueChange = onTextChanged,
        textStyle = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        ),
        colors = TextFieldDefaults.textFieldColors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            backgroundColor = Color.White,
            textColor = Color.Black
        ),
        placeholder = {
            Text(
                text = stringResource(id = R.string.hint_post_body),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Gray,
            )
        }
    )
}