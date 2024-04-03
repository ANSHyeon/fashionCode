package com.anshyeon.fashioncode.ui.screen.home.style.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.anshyeon.fashioncode.R
import com.anshyeon.fashioncode.data.model.Style
import com.anshyeon.fashioncode.data.model.User
import com.anshyeon.fashioncode.ui.component.appBar.BackButtonAppBar
import com.anshyeon.fashioncode.ui.component.loadingView.LoadingView
import com.anshyeon.fashioncode.ui.component.snackBar.TextSnackBarContainer
import com.anshyeon.fashioncode.ui.screen.home.style.home.StyleBox
import com.anshyeon.fashioncode.ui.screen.home.style.home.UserProfileDefault
import com.anshyeon.fashioncode.ui.screen.home.style.home.likeArea

@Composable
fun StyleDetailScreen(navController: NavHostController, style: Style) {

    val viewModel: StyleDetailViewModel = hiltViewModel()

    viewModel.getUser(style.writer)
    viewModel.getStyleList(style.writer)

    val userState by viewModel.user.collectAsStateWithLifecycle()
    val styleListState by viewModel.styleList.collectAsStateWithLifecycle()
    val userIdState by viewModel.userId.collectAsStateWithLifecycle()
    val isGetStyleListLoadingState by viewModel.isGetStyleListLoading.collectAsStateWithLifecycle()
    val isGetUserLoadingState by viewModel.isGetUserLoading.collectAsStateWithLifecycle()
    val isGetStyleListCompleteState by viewModel.isGetStyleListComplete.collectAsStateWithLifecycle()
    val isGetUserCompleteState by viewModel.isGetUserComplete.collectAsStateWithLifecycle()
    val isLoadingState by viewModel.isLoading.collectAsStateWithLifecycle()
    val snackBarTextState by viewModel.snackBarText.collectAsStateWithLifecycle()
    val showSnackBarState by viewModel.showSnackBar.collectAsStateWithLifecycle()

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
            if (isGetStyleListCompleteState && isGetUserCompleteState) {

                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .padding(8.dp),
                    columns = GridCells.Fixed(2)
                ) {
                    item(span = { GridItemSpan(2) }) {
                        StyleDetail(
                            modifier = Modifier
                                .padding(8.dp),
                            userIdState,
                            userState,
                            style = style,
                            styleListState,
                            { viewModel.createLike(it) },
                            { viewModel.deleteLike(it) },
                            { viewModel.navigateOtherUserProfile(navController, userIdState) }
                        )
                    }
                    if (styleListState.isNotEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Text(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .height(20.dp),
                                text = "${style.nickName}님의 다른 코디"
                            )
                        }
                    }
                    items(styleListState.filter { it.styleId != style.styleId }) { style ->
                        StyleBox(
                            modifier = Modifier,
                            userIdState,
                            style = style,
                            { viewModel.createLike(it) },
                            { viewModel.deleteLike(it) },
                            {
                                viewModel.navigateOtherStyleDetail(navController, style)
                            }
                        )
                    }
                }
            }
            LoadingView(
                isLoading = isLoadingState || isGetStyleListLoadingState || isGetUserLoadingState
            )
        }
    }
}

@Composable
fun StyleDetail(
    modifier: Modifier,
    userIdState: String,
    user: User?,
    style: Style,
    styleList: List<Style>,
    createLike: (String) -> Unit,
    deleteLike: (String) -> Unit,
    onNavigateOtherUserProfile: () -> Unit
) {
    val currentStyle =
        if (styleList.isEmpty()) null else styleList.first { it.styleId == style.styleId }

    Box(
        modifier = modifier
    ) {
        Column {
            Box(modifier = Modifier.clickable {
                onNavigateOtherUserProfile()
            }) {
                UserProfileDefault(Modifier.size(32.dp), 10.sp, user?.profileUrl, style.nickName)
            }
            AsyncImage(
                modifier = Modifier.fillMaxWidth(),
                model = currentStyle?.imageUrl,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.ic_place_holder)
            )
            likeArea(userIdState, style, { createLike(it) }, { deleteLike(it) })
        }
    }
}