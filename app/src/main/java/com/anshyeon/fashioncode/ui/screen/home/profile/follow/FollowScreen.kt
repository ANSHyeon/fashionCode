package com.anshyeon.fashioncode.ui.screen.home.profile.follow


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.data.model.Follow
import com.anshyeon.fashioncode.data.model.User
import com.anshyeon.fashioncode.ui.component.appBar.BackButtonAppBar
import com.anshyeon.fashioncode.ui.component.loadingView.LoadingView
import com.anshyeon.fashioncode.ui.component.snackBar.TextSnackBarContainer
import com.anshyeon.fashioncode.ui.screen.home.style.home.UserProfileDefault
import com.anshyeon.fashioncode.ui.theme.Gray
import com.anshyeon.fashioncode.ui.theme.SkyBlue
import kotlinx.coroutines.launch

@Composable
fun FollowScreen(navController: NavHostController, userList: List<User>, userId: String) {

    val viewModel: FollowViewModel = hiltViewModel()

    val followerListState by viewModel.followerList.collectAsStateWithLifecycle()
    val followingListState by viewModel.followingList.collectAsStateWithLifecycle()
    val myFollowingListState by viewModel.myFollowingList.collectAsStateWithLifecycle()
    val isGetFollowerLoadingState by viewModel.isGetFollowerLoading.collectAsStateWithLifecycle()
    val isGetFollowingLoadingState by viewModel.isGetFollowingLoading.collectAsStateWithLifecycle()
    val isGetMyFollowingLoadingState by viewModel.isGetMyFollowingLoading.collectAsStateWithLifecycle()
    val isGetFollowerCompleteState by viewModel.isGetFollowerComplete.collectAsStateWithLifecycle()
    val isGetFollowingCompleteState by viewModel.isGetFollowingComplete.collectAsStateWithLifecycle()
    val isGetMyFollowingCompleteState by viewModel.isGetMyFollowingComplete.collectAsStateWithLifecycle()
    val isLoadingState by viewModel.isLoading.collectAsStateWithLifecycle()
    val snackBarTextState by viewModel.snackBarText.collectAsStateWithLifecycle()
    val showSnackBarState by viewModel.showSnackBar.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = userId) {
        viewModel.getFollower(userId)
        viewModel.getFollowing(userId)
        viewModel.getMyFollowing()
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
            val isVisible =
                isGetFollowerCompleteState && isGetFollowingCompleteState && isGetMyFollowingCompleteState
            FollowItems(
                Modifier.padding(it),
                viewModel.myUserId,
                followerListState,
                followingListState,
                myFollowingListState,
                userList,
                isVisible,
                {
                    viewModel.navigateOtherUserProfile(navController, it)
                },
                {
                    viewModel.createFollow(it)
                    viewModel.addFollower(it)
                },
                {
                    viewModel.deleteFollow(it)
                    viewModel.removeFollower(it)
                }
            )

            LoadingView(
                isLoading = isLoadingState || isGetFollowerLoadingState || isGetFollowingLoadingState || isGetMyFollowingLoadingState
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FollowItems(
    modifier: Modifier,
    myUserId: String,
    followerList: List<Follow>,
    followingList: List<Follow>,
    myFollowingListState: List<Follow>,
    userList: List<User>,
    isVisible: Boolean,
    onNavigateOtherProfile: (String) -> Unit,
    addFollow: (String) -> Unit,
    removeFollow: (String) -> Unit
) {
    Column(
        modifier = modifier
    ) {

        val tabs = listOf("팔로워", "팔로잉")
        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState {
            tabs.size
        }

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            contentColor = Color.Black,
        ) {
            tabs.forEachIndexed { index, text ->
                Tab(
                    modifier = Modifier
                        .background(Color.White),
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = text,
                            fontWeight = FontWeight.Bold,
                        )
                    },
                )
            }
        }
        if (isVisible){
            HorizontalPager(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                state = pagerState,
                verticalAlignment = Alignment.Top
            ) { index ->
                if (index == 0) {
                    LazyColumn {
                        items(followerList) { follower ->
                            val user = userList.first { it.userId == follower.follower }
                            UserProfileWithFollowButton(
                                Modifier.size(32.dp),
                                myUserId == follower.follower,
                                myFollowingListState.any { it.following == follower.follower },
                                12.sp,
                                user.profileUrl,
                                user.nickName,
                                { onNavigateOtherProfile(follower.follower) },
                                { addFollow(follower.follower) },
                                { removeFollow(follower.follower) }
                            )
                        }
                    }
                } else {
                    LazyColumn {
                        items(followingList) { following ->
                            val user = userList.first { it.userId == following.following }
                            UserProfileWithFollowButton(
                                Modifier.size(32.dp),
                                myUserId == following.following,
                                myFollowingListState.any { it.following == following.following },
                                12.sp,
                                user.profileUrl,
                                user.nickName,
                                { onNavigateOtherProfile(following.following) },
                                { addFollow(following.following) },
                                { removeFollow(following.following) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserProfileWithFollowButton(
    modifier: Modifier,
    isMe: Boolean,
    isFollow: Boolean,
    textUnit: TextUnit,
    profileUrl: String?,
    nickName: String?,
    onNavigateOtherProfile: () -> Unit,
    createFollow: () -> Unit,
    deleteFollow: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .weight(3f)
                .clickable { onNavigateOtherProfile() },
        ) {
            UserProfileDefault(modifier, textUnit, profileUrl, nickName)
        }

        if (!isMe) {
            Button(
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isFollow) Gray else SkyBlue,
                    contentColor = if (isFollow) Color.Black else Color.White,
                ),
                enabled = true,
                onClick = {
                    if (isFollow) {
                        deleteFollow()
                    } else {
                        createFollow()
                    }
                }
            ) {
                Text(
                    text = if (isFollow) "팔로잉" else "팔로우",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}