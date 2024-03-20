package com.anshyeon.fashioncode.ui.screen.home.community.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.anshyeon.fashioncode.R
import com.anshyeon.fashioncode.data.model.Post
import com.anshyeon.fashioncode.ui.component.appBar.DefaultAppBar
import com.anshyeon.fashioncode.ui.component.loadingView.LoadingView
import com.anshyeon.fashioncode.ui.component.snackBar.TextSnackBarContainer
import com.anshyeon.fashioncode.ui.theme.DarkGray
import com.anshyeon.fashioncode.util.DateFormatText.getElapsedTime

@Composable
fun CommunityScreen(navController: NavHostController) {

    val viewModel: CommunityViewModel = hiltViewModel()

    val postListState by viewModel.postList.collectAsStateWithLifecycle()
    val isLoadingState by viewModel.isLoading.collectAsStateWithLifecycle()
    val snackBarTextState by viewModel.snackBarText.collectAsStateWithLifecycle()
    val showSnackBarState by viewModel.showSnackBar.collectAsStateWithLifecycle()


    TextSnackBarContainer(
        snackbarText = snackBarTextState,
        showSnackbar = showSnackBarState,
        onDismissSnackbar = { viewModel.dismissSnackBar() }
    ) {
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

                LazyColumn {
                    items(postListState) { post ->
                        PostContent(post) {

                        }
                    }
                }
            }
            LoadingView(
                isLoading = isLoadingState
            )
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
                        .height(24.dp),
                    text = post.title,
                    fontSize = 14.sp,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    modifier = Modifier
                        .height(16.dp),
                    text = post.body,
                    fontSize = 12.sp,
                    color = DarkGray,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(11.dp))
                Text(
                    modifier = Modifier
                        .height(16.dp),
                    text = getElapsedTime(post.createdDate),
                    fontSize = 10.sp,
                    color = DarkGray
                )
            }
            if (!post.profileImageUrl.isNullOrEmpty()) {
                AsyncImage(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .size(70.dp),
                    model = post.profileImageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_place_holder)
                )
            }
        }
    }
}