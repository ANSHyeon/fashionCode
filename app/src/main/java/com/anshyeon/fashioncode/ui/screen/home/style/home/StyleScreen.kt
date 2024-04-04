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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.anshyeon.fashioncode.ui.component.appBar.DefaultAppBar
import com.anshyeon.fashioncode.ui.component.loadingView.LoadingView
import com.anshyeon.fashioncode.ui.component.snackBar.TextSnackBarContainer
import com.anshyeon.fashioncode.ui.theme.Gray

@Composable
fun StyleScreen(navController: NavHostController) {

    val viewModel: StyleViewModel = hiltViewModel()

    val styleListState by viewModel.styleList.collectAsStateWithLifecycle()
    val userIdState by viewModel.userId.collectAsStateWithLifecycle()
    val isGetStyleListLoadingState by viewModel.isGetStyleListLoading.collectAsStateWithLifecycle()
    val snackBarTextState by viewModel.snackBarText.collectAsStateWithLifecycle()
    val showSnackBarState by viewModel.showSnackBar.collectAsStateWithLifecycle()


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
                items(styleListState) { style ->
                    StyleBox(
                        modifier = Modifier,
                        userIdState,
                        style = style,
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
    userIdState: String,
    style: Style,
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
                style.profileImageUrl,
                style.nickName
            )
            AsyncImage(
                modifier = Modifier.fillMaxWidth(),
                model = style.imageUrl,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.ic_place_holder)
            )
            likeArea(userIdState, style, { createLike(it) }, { deleteLike(it) })
        }
    }
}

@Composable
fun likeArea(
    userIdState: String,
    style: Style,
    createLike: (String) -> Unit,
    deleteLike: (String) -> Unit
) {
    Row {
        var isCheck by remember { mutableStateOf(style.likeList.any { it == userIdState }) }
        var likeCount by remember { mutableStateOf(style.likeList.size) }

        IconButton(
            onClick = {
                if (isCheck) {
                    deleteLike(style.styleId)
                    likeCount--
                } else {
                    createLike(style.styleId)
                    likeCount++
                }
                isCheck = !isCheck
            }
        ) {
            if (isCheck) {
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
            text = "${likeCount} likes"
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