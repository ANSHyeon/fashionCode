package com.anshyeon.fashioncode.ui.screen.signin.info

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anshyeon.fashioncode.R
import com.anshyeon.fashioncode.ui.component.button.RectangleButton

@Composable
fun InfoInputScreen() {

    val viewModel: InfoInputViewModel = hiltViewModel()

    val nickNameState by viewModel.nickName.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        SetUserInfo(nickNameState) { newNickName ->
        }
        RectangleButton(text = "시작하기") {
        }
    }
}

@Composable
private fun SetUserInfo(nickName: String, onChanged: (newNickName: String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.fillMaxHeight(0.1f))
        Box(modifier = Modifier) {
            Image(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = null
            )
            Canvas(
                modifier = Modifier
                    .size(34.dp)
                    .align(Alignment.BottomEnd)
            ) {
                drawCircle(
                    color = Color.Gray,
                )
            }
            Image(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(7.dp),
                painter = painterResource(id = R.drawable.ic_camera),
                contentDescription = null
            )
        }
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
                    text = "닉네임을 입력해주세요.",
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