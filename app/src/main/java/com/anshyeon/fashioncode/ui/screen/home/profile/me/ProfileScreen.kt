package com.anshyeon.fashioncode.ui.screen.home.profile.me

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.anshyeon.fashioncode.R
import com.anshyeon.fashioncode.data.model.Clothes
import com.anshyeon.fashioncode.data.model.ClothesType
import com.anshyeon.fashioncode.data.model.Follow
import com.anshyeon.fashioncode.data.model.Style
import com.anshyeon.fashioncode.data.model.User
import com.anshyeon.fashioncode.ui.component.appBar.DefaultAppBar
import com.anshyeon.fashioncode.ui.component.loadingView.LoadingView
import com.anshyeon.fashioncode.ui.component.snackBar.TextSnackBarContainer
import com.anshyeon.fashioncode.ui.screen.home.profile.other.TotalUserProfile
import com.anshyeon.fashioncode.ui.screen.home.style.create.CodiItems
import com.anshyeon.fashioncode.ui.theme.Gray
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navController: NavHostController) {

    val viewModel: ProfileViewModel = hiltViewModel()
    val context = LocalContext.current

    val userState by viewModel.user.collectAsStateWithLifecycle()
    val styleListState by viewModel.styleList.collectAsStateWithLifecycle()
    val followerListState by viewModel.followerList.collectAsStateWithLifecycle()
    val followingListState by viewModel.followingList.collectAsStateWithLifecycle()
    val clothesListState by viewModel.clothesList.collectAsStateWithLifecycle()
    val isCutOutLoadingState by viewModel.isCutOutLoading.collectAsStateWithLifecycle()
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

    val takePhotoFromCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { takenPhoto ->
            if (takenPhoto != null) {
                viewModel.cutoutImage(context, takenPhoto)
            }
        }
    )

    Scaffold(
        topBar = {
            DefaultAppBar(stringResource(id = R.string.label_app_bar_my_profile))
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
                Column(
                    Modifier
                        .padding(it)
                ) {
                    ProfileBox(
                        Modifier.padding(16.dp),
                        userState,
                        styleListState,
                        followerListState,
                        followingListState,
                        { viewModel.navigateProfileEdit(navController) }
                    ) {
                        viewModel.navigateFollow(navController)
                    }
                    MyItems(
                        Modifier.padding(top = 8.dp),
                        styleListState,
                        clothesListState,
                    ) {
                        viewModel.changeClothesType(it)
                        takePhotoFromCameraLauncher.launch()
                    }
                }
            }

            LoadingView(
                isLoading = isLoadingState || isGetUserLoadingState || isGetStyleListLoadingState ||
                        isGetFollowerLoadingState || isGetFollowingLoadingState || isCutOutLoadingState
            )
        }
    }
}

@Composable
private fun ProfileBox(
    modifier: Modifier,
    userState: User?,
    styleListState: List<Style>,
    followerListState: List<Follow>,
    followingListState: List<Follow>,
    navigateProfileEdit: () -> Unit,
    navigateFollow: () -> Unit
) {
    Column(modifier = modifier) {
        TotalUserProfile(
            modifier = Modifier.size(72.dp),
            textUnit = 14.sp,
            profileUrl = userState?.profileUrl,
            nickName = userState?.nickName,
            codiCount = styleListState.size,
            followerCount = followerListState.size,
            followingCount = followingListState.size,
        ) { navigateFollow() }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Gray,
                contentColor = Color.Black,
            ),
            enabled = true,
            onClick = {
                navigateProfileEdit()
            }
        ) {
            Text(
                text = "프로필 수정",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyItems(
    modifier: Modifier,
    styleList: List<Style>,
    clothesList: List<Clothes>,
    onAddButtonClick: (ClothesType) -> Unit,
) {
    Column(
        modifier = modifier
    ) {

        val tabs = listOf("스타일", "옷장")
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

        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            state = pagerState,
            verticalAlignment = Alignment.Top
        ) { index ->
            if (index == 0) {
                StyleItems(
                    styleList = styleList
                )
            } else {
                CodiItems(
                    Modifier
                        .fillMaxWidth(),
                    clothesList,
                    {
                        onAddButtonClick(it)
                    },
                    {}
                )
            }
        }
    }
}

@Composable
fun StyleItems(
    modifier: Modifier = Modifier,
    styleList: List<Style>
) {
    LazyVerticalGrid(
        modifier = modifier
            .border(1.dp, Gray)
            .padding(1.dp),
        columns = GridCells.Fixed(2)
    ) {
        items(styleList) { style ->
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Gray),
                model = style.imageUrl,
                contentDescription = null
            )
        }
    }
}