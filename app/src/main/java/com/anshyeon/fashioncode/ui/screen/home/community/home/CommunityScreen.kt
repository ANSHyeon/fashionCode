package com.anshyeon.fashioncode.ui.screen.home.community.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.anshyeon.fashioncode.R
import com.anshyeon.fashioncode.data.model.Post
import com.anshyeon.fashioncode.ui.component.appBar.DefaultAppBar
import com.anshyeon.fashioncode.ui.theme.DarkGray

@Composable
fun CommunityScreen(navController: NavHostController) {

    val viewModel: CommunityViewModel = hiltViewModel()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            DefaultAppBar(stringResource(id = R.string.label_app_bar_community)) {
                IconButton(
                    onClick = { viewModel.navigateCommunityWrite(navController) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Icon",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun PostContent(post: Post, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(15.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    modifier = Modifier
                        .height(24.dp)
                        .background(Color.Blue),
                    text = post.title,
                    fontSize = 14.sp,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    modifier = Modifier
                        .height(16.dp)
                        .background(Color.Green),
                    text = post.body,
                    fontSize = 12.sp,
                    color = DarkGray,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(11.dp))
                Text(
                    modifier = Modifier
                        .height(16.dp)
                        .background(Color.Red),
                    text = post.title,
                    fontSize = 10.sp,
                    color = DarkGray
                )
            }
            if (post.imageUrlList.isNotEmpty()) {
                AsyncImage(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .size(70.dp),
                    model = post.imageUrlList.first(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_launcher_background)
                )
            }
        }
    }
}