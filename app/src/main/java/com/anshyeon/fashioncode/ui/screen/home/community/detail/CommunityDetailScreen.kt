package com.anshyeon.fashioncode.ui.screen.home.community.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.anshyeon.fashioncode.R
import com.anshyeon.fashioncode.data.model.Post
import com.anshyeon.fashioncode.data.model.User
import com.anshyeon.fashioncode.ui.component.appBar.BackButtonAppBar
import com.anshyeon.fashioncode.ui.component.loadingView.LoadingView
import com.anshyeon.fashioncode.ui.component.snackBar.TextSnackBarContainer
import com.anshyeon.fashioncode.ui.theme.DarkGray
import com.anshyeon.fashioncode.ui.theme.Gray
import com.anshyeon.fashioncode.util.DateFormatText

@Composable
fun CommunityDetailScreen(navController: NavHostController, postId: String) {

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            BackButtonAppBar {
            }
        },
        bottomBar = {
            CommentSubmit("", { }) {
            }
        }
    ) {
        TextSnackBarContainer(
            snackbarText = "snackBarTextState",
            showSnackbar = false,
            onDismissSnackbar = { }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(10.dp)
                    .verticalScroll(scrollState)
            ) {
//                if (isGetCompleteState) {
//                    DetailContent(postState, userState)
//                }
            }
            LoadingView(
                isLoading = false
            )

        }

    }

}

@Composable
fun DetailContent(post: Post?, user: User?) {
    UserProfileDefault(post?.createdDate, user)
    Spacer(modifier = Modifier.size(5.dp))
    Text(
        text = post?.title ?: "제목을 불러올 수 없습니다.",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = post?.body ?: "내용을 불러올 수 없습니다.",
        fontSize = 18.sp
    )
    ImageList(post)
}

@Composable
fun UserProfileDefault(createDate: String?, user: User?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (user?.profileUrl == null) {
            Image(
                modifier = Modifier.size(40.dp),
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = null
            )
        } else {
            AsyncImage(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape),
                model = user.profileUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        Column {
            Text(
                text = user?.nickName ?: "닉네임을 불러올 수 없습니다.",
                fontWeight = FontWeight.Bold
            )
            Text(
                text = DateFormatText.getElapsedTime(createDate),
                color = DarkGray
            )
        }
    }
}

@Composable
private fun ImageList(post: Post?) {
    post?.imageUrlList?.forEach { url ->
        AsyncImage(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_place_holder)
        )
    }
}

@Composable
private fun CommentSubmit(
    text: String,
    onTextChanged: (body: String) -> Unit,
    onclick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            modifier = Modifier
                .height(30.dp)
                .weight(1f),
            value = text,
            onValueChange = { onTextChanged(it) },
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .background(color = Gray, shape = RoundedCornerShape(size = 5.dp)),
                    contentAlignment = Alignment.CenterStart

                ) {
                    innerTextField()
                }
            },
        )
        IconButton(
            modifier = Modifier
                .size(24.dp),
            onClick = {
                if (text.isNotEmpty()) {
                    onclick()
                }
            }) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null,
            )
        }
    }
}