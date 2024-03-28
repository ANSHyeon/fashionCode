package com.anshyeon.fashioncode.ui.screen.home.style.create

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.anshyeon.fashioncode.R
import com.anshyeon.fashioncode.data.model.Clothes
import com.anshyeon.fashioncode.data.model.ClothesType
import com.anshyeon.fashioncode.ui.component.appBar.BackButtonWithActionAppBar
import com.anshyeon.fashioncode.ui.component.loadingView.LoadingView
import com.anshyeon.fashioncode.ui.component.snackBar.TextSnackBarContainer
import com.anshyeon.fashioncode.ui.theme.DarkGray
import com.anshyeon.fashioncode.ui.theme.Gray
import kotlinx.coroutines.launch

@Composable
fun StyleCreateScreen(navController: NavHostController) {

    val viewModel: StyleCreateViewModel = hiltViewModel()
    val context = LocalContext.current

    val clothesListState by viewModel.clothesList.collectAsStateWithLifecycle()
    val isLoadingState by viewModel.isLoading.collectAsStateWithLifecycle()
    val isCutOutLoadingState by viewModel.isCutOutLoading.collectAsStateWithLifecycle()
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
            BackButtonWithActionAppBar(stringResource(id = R.string.label_app_bar_style_create),
                { viewModel.navigateBack(navController) }) {
            }
        }
    ) {
        TextSnackBarContainer(
            snackbarText = snackBarTextState,
            showSnackbar = showSnackBarState,
            onDismissSnackbar = { viewModel.dismissSnackBar() }
        ) {
            Box(
                modifier = Modifier
                    .padding(it)
            ) {
                Column {
                    CodiCanvas(
                        Modifier
                            .weight(5f)
                            .fillMaxWidth()
                            .background(Gray)
                    )
                    CodiItems(
                        Modifier
                            .weight(4f)
                            .fillMaxWidth(),
                        clothesListState
                    ) {
                        viewModel.changeClothesType(it)
                        takePhotoFromCameraLauncher.launch()
                    }
                }
                LoadingView(
                    isLoading = isLoadingState || isCutOutLoadingState
                )
            }
        }
    }
}

@Composable
fun CodiCanvas(modifier: Modifier) {
    Box(
        modifier = modifier
    ) {
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CodiItems(
    modifier: Modifier,
    clothesListState: List<Clothes>,
    onClick: (ClothesType) -> Unit
) {
    Column(
        modifier = modifier
    ) {

        val tabs = ClothesType.values().takeLast(7)
        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState {
            tabs.size
        }
        val density = LocalDensity.current
        val tabWidths = remember {
            val tabWidthStateList = mutableStateListOf<Dp>()
            repeat(tabs.size) {
                tabWidthStateList.add(0.dp)
            }
            tabWidthStateList
        }

        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            contentColor = Color.Black,
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.customTabIndicatorOffset(
                        currentTabPosition = tabPositions[pagerState.currentPage],
                        tabWidth = tabWidths[pagerState.currentPage]
                    )
                )
            }
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
                            text = text.name,
                            fontWeight = FontWeight.Bold,
                            onTextLayout = { textLayoutResult ->
                                tabWidths[index] =
                                    with(density) { textLayoutResult.size.width.toDp() }
                            }
                        )
                    },
                )
            }
        }

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            verticalAlignment = Alignment.Top
        ) { index ->
            val categoryClothesList = clothesListState.filter {
                it.type == tabs[index] || it.type == ClothesType.ADD
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(4)
            ) {
                items(categoryClothesList) {
                    if (it.type == ClothesType.ADD) {
                        CodiItemAddButton {
                            onClick(tabs[index])
                        }
                    } else {
                        CodiItem(clothes = it) {
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CodiItemAddButton(onClick: () -> Unit) {
    val stroke =
        Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
    Box(
        Modifier
            .size(100.dp)
            .background(Color.White)
            .padding(5.dp)
            .clickable {
                onClick()
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = DarkGray,
                cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                style = stroke
            )
        }
        Icon(
            modifier = Modifier
                .align(Alignment.Center)
                .size(72.dp),
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = DarkGray
        )
    }
}

@Composable
fun CodiItem(clothes: Clothes, onClick: () -> Unit) {
    Box(
        Modifier
            .size(100.dp)
            .padding(5.dp)
            .clickable {
                onClick()
            }
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp)),
            model = clothes.image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(Gray)
        )
    }
}

fun Modifier.customTabIndicatorOffset(
    currentTabPosition: TabPosition,
    tabWidth: Dp
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "customTabIndicatorOffset"
        value = currentTabPosition
    }
) {
    val currentTabWidth by animateDpAsState(
        targetValue = tabWidth,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing), label = ""
    )
    val indicatorOffset by animateDpAsState(
        targetValue = ((currentTabPosition.left + currentTabPosition.right - tabWidth) / 2),
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing), label = ""
    )
    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = indicatorOffset)
        .width(currentTabWidth)
}