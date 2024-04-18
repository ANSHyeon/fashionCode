package com.anshyeon.fashioncode.ui.screen.home.style.create

import android.graphics.Picture
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
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
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
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
import com.anshyeon.fashioncode.ui.graph.DetailHomeScreen
import com.anshyeon.fashioncode.ui.theme.DarkGray
import com.anshyeon.fashioncode.ui.theme.Gray
import com.anshyeon.fashioncode.util.ImageTypeConvertor
import kotlinx.coroutines.launch

@Composable
fun StyleCreateScreen(navController: NavHostController, selectedDate: String) {

    val viewModel: StyleCreateViewModel = hiltViewModel()
    val context = LocalContext.current
    val picture = remember { Picture() }

    val clothesListState by viewModel.clothesList.collectAsStateWithLifecycle()
    val selectedClothesListState by viewModel.selectedClothesList.collectAsStateWithLifecycle()
    val isCreateStyleLoadingState by viewModel.isCreateStyleLoading.collectAsStateWithLifecycle()
    val isCutOutLoadingState by viewModel.isCutOutLoading.collectAsStateWithLifecycle()
    val isInsertStyleLoadingState by viewModel.isInsertStyleLoading.collectAsStateWithLifecycle()
    val isDeleteClothesLoadingState by viewModel.isDeleteClothesLoading.collectAsStateWithLifecycle()
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
                if (selectedDate == DetailHomeScreen.StyleCreate.route) {
                    viewModel.createStyle(
                        navController,
                        ImageTypeConvertor.createBitmapFromPicture(picture)
                    )
                } else {
                    viewModel.insertStyle(
                        navController,
                        ImageTypeConvertor.createBitmapFromPicture(picture),
                        selectedDate
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
            Box(
                modifier = Modifier
                    .padding(it)
            ) {
                Column {
                    CodiCanvas(
                        Modifier
                            .weight(5f)
                            .fillMaxWidth()
                            .background(Gray),
                        picture,
                        selectedClothesListState
                    ) {
                        viewModel.removeSelectedClothes(it)
                    }
                    CodiItems(
                        Modifier
                            .weight(4f)
                            .fillMaxWidth(),
                        clothesListState,
                        { viewModel.deleteClothes(it) },
                        {
                            viewModel.changeClothesType(it)
                            takePhotoFromCameraLauncher.launch()
                        },
                        {
                            viewModel.addSelectedClothes(it)
                        }
                    )
                }
                LoadingView(
                    isLoading = isCreateStyleLoadingState || isCutOutLoadingState || isInsertStyleLoadingState || isDeleteClothesLoadingState
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CodiCanvas(
    modifier: Modifier,
    picture: Picture,
    clothesListState: List<Clothes>,
    removeClothes: (Int) -> Unit
) {
    Box(
        modifier = modifier
            .drawWithCache {
                val width = this.size.width.toInt()
                val height = this.size.height.toInt()
                onDrawWithContent {
                    val pictureCanvas =
                        androidx.compose.ui.graphics.Canvas(
                            picture.beginRecording(
                                width,
                                height
                            )
                        )
                    draw(this, this.layoutDirection, pictureCanvas, this.size) {
                        this@onDrawWithContent.drawContent()
                    }
                    picture.endRecording()

                    drawIntoCanvas { canvas -> canvas.nativeCanvas.drawPicture(picture) }
                }
            }
    ) {
        var removeClotheIndex by remember { mutableStateOf(9999) }
        var showDialog by remember { mutableStateOf(false) }
        var zIndexCount by remember { mutableStateOf(1) }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {},
                text = { Text(stringResource(id = R.string.message_dialog_removee)) },
                dismissButton = {
                    Button(onClick = {
                        showDialog = false
                    }) {
                        Text("CANCEL")
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        removeClothes(removeClotheIndex)
                        showDialog = false
                    }) {
                        Text("OK")
                    }
                }
            )
        }
        clothesListState.forEachIndexed { index, clothes ->
            var scale by remember { mutableStateOf(1f) }
            var rotation by remember { mutableStateOf(0f) }
            var offset by remember { mutableStateOf(Offset.Zero) }
            val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
                scale *= zoomChange
                rotation += rotationChange
                offset += offsetChange
            }
            var zIndex by remember { mutableStateOf(0f) }
            var isAsyncImageLoaded by remember { mutableStateOf(false) }
            if (isAsyncImageLoaded) {
                Box {}
            }
            AsyncImage(
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        rotationZ = rotation,
                        translationX = offset.x,
                        translationY = offset.y
                    )
                    .transformable(state = state)
                    .size(200.dp * scale)
                    .combinedClickable(
                        onLongClick = {
                            removeClotheIndex = index
                            showDialog = true
                        },
                        onClick = { zIndex = zIndexCount++.toFloat() }
                    )
                    .zIndex(zIndex),
                model = clothes.image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(Gray),
                onSuccess = {
                    isAsyncImageLoaded = true
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CodiItems(
    modifier: Modifier,
    clothesListState: List<Clothes>,
    removeClothes: (Clothes) -> Unit,
    onAddButtonClick: (ClothesType) -> Unit,
    onCodiItemClick: (Clothes) -> Unit
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
                            onAddButtonClick(tabs[index])
                        }
                    } else {
                        CodiItem(clothes = it,
                            { clothes -> removeClothes(clothes) }
                        ) {
                            onCodiItemClick(it)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CodiItem(clothes: Clothes, removeClothes: (Clothes) -> Unit, onClick: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {},
            text = { Text(stringResource(id = R.string.message_dialog_removee)) },
            dismissButton = {
                Button(onClick = {
                    showDialog = false
                }) {
                    Text("CANCEL")
                }
            },
            confirmButton = {
                Button(onClick = {
                    removeClothes(clothes)
                    showDialog = false
                }) {
                    Text("OK")
                }
            }
        )
    }
    Box(
        Modifier
            .size(100.dp)
            .padding(5.dp)
            .combinedClickable(
                onLongClick = {
                    showDialog = true
                },
                onClick = { onClick() }
            )
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