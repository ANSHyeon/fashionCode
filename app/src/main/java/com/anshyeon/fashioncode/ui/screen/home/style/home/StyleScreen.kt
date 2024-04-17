package com.anshyeon.fashioncode.ui.screen.home.style.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.anshyeon.fashioncode.R
import com.anshyeon.fashioncode.data.model.Style
import com.anshyeon.fashioncode.data.model.User
import com.anshyeon.fashioncode.ui.component.appBar.DefaultAppBar
import com.anshyeon.fashioncode.ui.component.loadingView.LoadingView
import com.anshyeon.fashioncode.ui.component.snackBar.TextSnackBarContainer
import com.anshyeon.fashioncode.ui.theme.Gray

@Composable
fun StyleScreen(navController: NavHostController, userList: List<User>) {

    val viewModel: StyleViewModel = hiltViewModel()

    val styleListState by viewModel.styleList.collectAsStateWithLifecycle()
    val isGetStyleListLoadingState by viewModel.isGetStyleListLoading.collectAsStateWithLifecycle()
    val isNavigateState by viewModel.isNavigate.collectAsStateWithLifecycle()
    val snackBarTextState by viewModel.snackBarText.collectAsStateWithLifecycle()
    val showSnackBarState by viewModel.showSnackBar.collectAsStateWithLifecycle()

    if (isNavigateState) {
        viewModel.getStyleList()
        viewModel.clearIsNavigate()
    }
    Scaffold(
        topBar = {
            DefaultAppBar(stringResource(id = R.string.app_name)) {
                IconButton(
                    onClick = { viewModel.navigateStyleCreate(navController) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Icon",
                        tint = Color.Black
                    )
                }


            }
        }
    ) {
        TextSnackBarContainer(
            snackbarText = snackBarTextState,
            showSnackbar = showSnackBarState,
            onDismissSnackbar = { viewModel.dismissSnackBar() }
        ) {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(8.dp),
                columns = GridCells.Fixed(2)
            ) {
                itemsIndexed(styleListState) { index, style ->
                    val user = userList.first { it.userId == style.writer }
                    StyleBox(
                        modifier = Modifier,
                        style = style,
                        user = user,
                        { isCheck, count -> viewModel.setStyleLike(index, isCheck, count) },
                        { viewModel.createLike(it) },
                        { viewModel.deleteLike(it) },
                        {
                            viewModel.navigateStyleDetail(navController, style)
                        }
                    )
                }
            }
            LoadingView(
                isLoading = isGetStyleListLoadingState
            )
        }
    }
}

@Composable
fun StyleBox(
    modifier: Modifier,
    style: Style,
    user: User,
    setLike: (Boolean, Int) -> Unit,
    createLike: (String) -> Unit,
    deleteLike: (String) -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .border(1.dp, Gray, RoundedCornerShape(10.dp))
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column {
            UserProfileDefault(
                Modifier.size(32.dp),
                10.sp,
                user.profileUrl,
                user.nickName
            )
            AsyncImage(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                model = style.imageUrl,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.ic_place_holder)
            )
            likeArea(
                style,
                { isCheck, count -> setLike(isCheck, count) },
                { createLike(it) },
                { deleteLike(it) })
        }
    }
}

@Composable
fun likeArea(
    style: Style,
    setLike: (Boolean, Int) -> Unit,
    createLike: (String) -> Unit,
    deleteLike: (String) -> Unit
) {
    Row {
        IconButton(
            onClick = {
                if (style.isLike!!) {
                    deleteLike(style.styleId)
                    setLike(false, style.likeCount!! - 1)
                } else {
                    createLike(style.styleId)
                    setLike(true, style.likeCount!! + 1)
                }
            }
        ) {
            if (style.isLike!!) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = Color.Red
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.FavoriteBorder,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        }

        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = "${style.likeCount} likes"
        )
    }
}

@Composable
fun UserProfileDefault(
    modifier: Modifier,
    textUnit: TextUnit,
    profileUrl: String?,
    nickName: String?,
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
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = nickName ?: "닉네임을 불러올 수 없습니다.",
            fontSize = textUnit,
            fontWeight = FontWeight.Bold
        )
    }
}