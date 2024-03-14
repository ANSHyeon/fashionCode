package com.anshyeon.fashioncode.ui.screen.signin.info

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.anshyeon.fashioncode.R
import com.anshyeon.fashioncode.ui.component.button.RectangleButton
import com.anshyeon.fashioncode.ui.theme.gray
import com.anshyeon.fashioncode.util.isValidNickname

@Composable
fun InfoInputScreen() {

    val viewModel: InfoInputViewModel = hiltViewModel()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.changeImageUri(uri)
    }

    val nickNameState by viewModel.nickName.collectAsStateWithLifecycle()
    val imageUriState by viewModel.imageUri.collectAsStateWithLifecycle()
    val nextButtonVisibility = isValidNickname(nickNameState)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        SetUserInfo(imageUriState, nickNameState, launcher) { newNickName ->
            viewModel.changeNickName(newNickName)
        }
        RectangleButton(
            text = stringResource(id = R.string.label_start),
            visibility = nextButtonVisibility
        ) {
            viewModel.saveUserInfo(context)
        }
    }
}

@Composable
private fun SetUserInfo(
    imageUri: Uri?,
    nickName: String,
    launcher: ManagedActivityResultLauncher<String, Uri?>,
    onChanged: (newNickName: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.fillMaxHeight(0.1f))
        SetProfileImage(imageUri) { launcher.launch("image/*") }
        Spacer(modifier = Modifier.fillMaxHeight(0.1f))
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 16.dp),
            value = nickName,
            onValueChange = { nickName ->
                onChanged(nickName)
            },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.hint_info_input_nickname),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray
                    ),
                    textAlign = TextAlign.Center
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        )
    }
}

@Composable
private fun SetProfileImage(imageUri: Uri?, onclick: () -> Unit) {

    Box(modifier = Modifier) {
        if (imageUri == null) {
            Image(
                modifier = Modifier.size(100.dp),
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = null
            )
        } else {
            AsyncImage(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                model = imageUri,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        Canvas(
            modifier = Modifier
                .size(34.dp)
                .align(Alignment.BottomEnd)
        ) {
            drawCircle(
                color = gray,
            )
        }
        IconButton(
            modifier = Modifier
                .size(34.dp)
                .align(Alignment.BottomEnd)
                .padding(7.dp),
            onClick = { onclick() }) {
            Icon(
                modifier = Modifier
                    .align(Alignment.BottomEnd),
                painter = painterResource(id = R.drawable.ic_camera),
                contentDescription = null
            )
        }
    }
}