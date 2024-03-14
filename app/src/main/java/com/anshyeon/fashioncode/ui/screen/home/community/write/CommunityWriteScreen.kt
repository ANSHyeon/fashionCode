package com.anshyeon.fashioncode.ui.screen.home.community.write

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.anshyeon.fashioncode.R
import com.anshyeon.fashioncode.ui.component.appBar.BackButtonAppBar
import com.anshyeon.fashioncode.ui.theme.gray

@Composable
fun CommunityWriteScreen(navController: NavHostController) {

    val viewModel: CommunityWriteViewModel = hiltViewModel()
    val selectedImageList by viewModel.selectedImageList.collectAsStateWithLifecycle()

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris ->
            if (selectedImageList.size + uris.size <= 10) {
                viewModel.addImageUris(uris)
            }
        }
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            BackButtonAppBar(title = stringResource(id = R.string.label_app_bar_community_write)) {
                viewModel.navigateBack(navController)
            }
            Spacer(modifier = Modifier.height(17.dp))
            ImageSelector(selectedImageList, multiplePhotoPickerLauncher) { index ->
                viewModel.removeImageUris(index)
            }
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
                color = gray,
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