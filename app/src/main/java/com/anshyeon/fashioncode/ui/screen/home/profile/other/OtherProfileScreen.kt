package com.anshyeon.fashioncode.ui.screen.home.profile.other

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.anshyeon.fashioncode.R
import com.anshyeon.fashioncode.ui.component.appBar.BackButtonAppBar
import com.anshyeon.fashioncode.ui.component.loadingView.LoadingView
import com.anshyeon.fashioncode.ui.component.snackBar.TextSnackBarContainer
import com.anshyeon.fashioncode.ui.screen.home.style.home.StyleBox
import com.anshyeon.fashioncode.ui.theme.Gray
import com.anshyeon.fashioncode.ui.theme.SkyBlue

@Composable
fun OtherProfileScreen(navController: NavHostController, userId: String) {

    val viewModel: OtherProfileViewModel = hiltViewModel()

    val userState by viewModel.user.collectAsStateWithLifecycle()
    val styleListState by viewModel.styleList.collectAsStateWithLifecycle()
    val followerListState by viewModel.followerList.collectAsStateWithLifecycle()
    val followingListState by viewModel.followingList.collectAsStateWithLifecycle()
    val isGetStyleListLoadingState by viewModel.isGetStyleListLoading.collectAsStateWithLifecycle()
    val isGetUserLoadingState by viewModel.isGetUserLoading.collectAsStateWithLifecycle()
    val isGetFollowerLoadingState by viewModel.isGetFollowerLoading.collectAsStateWithLifecycle()
    val isGetFollowingLoadingState by viewModel.isGetFollowingLoading.collectAsStateWithLifecycle()
    val isGetStyleListCompleteState by viewModel.isGetStyleListComplete.collectAsStateWithLifecycle()
    val isGetUserCompleteState by viewModel.isGetUserComplete.collectAsStateWithLifecycle()
    val isGetFollowerCompleteState by viewModel.isGetFollowerComplete.collectAsStateWithLifecycle()
    val isGetFollowingCompleteState by viewModel.isGetFollowingComplete.collectAsStateWithLifecycle()
    val isLoadingState by viewModel.isLoading.collectAsStateWithLifecycle()
    val snackBarTextState by viewModel.snackBarText.collectAsStateWithLifecycle()
    val showSnackBarState by viewModel.showSnackBar.collectAsStateWithLifecycle()

    if (!isGetUserCompleteState) {
        viewModel.getUser(userId)
        viewModel.getFollower(userId)
        viewModel.getFollowing(userId)
        viewModel.getStyleList(userId)
    }

    Scaffold(
        topBar = {
            BackButtonAppBar("") {
                viewModel.navigateBack(navController)
            }
        }
    ) {
        TextSnackBarContainer(
            snackbarText = snackBarTextState,
            showSnackbar = showSnackBarState,
            onDismissSnackbar = { viewModel.dismissSnackBar() }
        ) {

            if (isGetStyleListCompleteState && isGetUserCompleteState &&
                isGetFollowerCompleteState && isGetFollowingCompleteState
            ) {
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .padding(8.dp),
                    columns = GridCells.Fixed(2)
                ) {
                    item(span = { GridItemSpan(2) }) {
                        Column {
                            OtherUserProfile(
                                modifier = Modifier.size(72.dp),
                                textUnit = 14.sp,
                                profileUrl = userState?.profileUrl,
                                nickName = userState?.nickName,
                                codiCount = styleListState.size,
                                followerCount = followerListState.size,
                                followingCount = followingListState.size,
                            ) { viewModel.navigateFollow(navController, userId) }

                            if (viewModel.myUserId != userState?.userId) {
                                val isFollow =
                                    followerListState.any { it.follower == viewModel.myUserId }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 5.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = if (isFollow) Gray else SkyBlue,
                                        contentColor = if (isFollow) Color.Black else Color.White,
                                    ),
                                    enabled = true,
                                    onClick = {
                                        if (isFollow) {
                                            viewModel.deleteFollow(userState?.userId)
                                            viewModel.removeFollower()
                                        } else {
                                            viewModel.createFollow(userState?.userId)
                                            viewModel.addFollower(userState?.userId)
                                        }
                                    }
                                ) {
                                    Text(
                                        text = if (true) "팔로우" else "팔로잉",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    if (styleListState.isNotEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Text(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .padding(top = 8.dp)
                                    .height(20.dp),
                                text = "${userState?.nickName}님의 코디"
                            )
                        }
                    }
                    itemsIndexed(styleListState) { index, style ->
                        StyleBox(
                            modifier = Modifier,
                            style = style,
                            { isCheck, count ->
                                viewModel.setStyleLike(
                                    index,
                                    isCheck,
                                    count
                                )
                            },
                            { viewModel.createLike(it) },
                            { viewModel.deleteLike(it) },
                            {
                                viewModel.navigateStyleDetail(navController, style)
                            }
                        )
                    }
                }
            }

            LoadingView(
                isLoading = isLoadingState || isGetUserLoadingState || isGetStyleListLoadingState ||
                        isGetFollowerLoadingState || isGetFollowingLoadingState
            )
        }
    }
}

@Composable
fun OtherUserProfile(
    modifier: Modifier,
    textUnit: TextUnit,
    profileUrl: String?,
    nickName: String?,
    codiCount: Int?,
    followerCount: Int?,
    followingCount: Int?,
    navigateFollow: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (profileUrl == null) {
            Image(
                modifier = modifier,
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = null
            )
        } else {
            AsyncImage(
                modifier = modifier
                    .clip(CircleShape),
                model = profileUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(top = 10.dp, bottom = 5.dp),
                text = nickName ?: "닉네임을 불러올 수 없습니다.",
                fontSize = textUnit,
                fontWeight = FontWeight.Bold
            )
            Row {
                OtherUserProfileText(
                    Modifier.padding(10.dp),
                    "코디 ${codiCount}"
                )
                OtherUserProfileText(
                    Modifier
                        .padding(10.dp)
                        .clickable { navigateFollow() },
                    "팔로워 ${followerCount}"
                )
                OtherUserProfileText(
                    Modifier
                        .padding(10.dp)
                        .clickable { navigateFollow() },
                    "팔로잉 ${followingCount}"
                )
            }
        }
    }
}

@Composable
private fun OtherUserProfileText(modifier: Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    )
}