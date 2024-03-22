package com.anshyeon.fashioncode.ui.screen.home.style.create

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.R
import com.anshyeon.fashioncode.ui.component.appBar.BackButtonAppBar
import com.anshyeon.fashioncode.ui.component.loadingView.LoadingView
import com.anshyeon.fashioncode.ui.component.snackBar.TextSnackBarContainer

@Composable
fun StyleCreateScreen(navController: NavHostController) {

    val viewModel: StyleCreateViewModel = hiltViewModel()

    val isLoadingState by viewModel.isLoading.collectAsStateWithLifecycle()
    val snackBarTextState by viewModel.snackBarText.collectAsStateWithLifecycle()
    val showSnackBarState by viewModel.showSnackBar.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            BackButtonAppBar(stringResource(id = R.string.label_app_bar_style_create)) {
                viewModel.navigateBack(navController)
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
                    )
                    CodiItems(
                        Modifier
                            .weight(4f)
                            .fillMaxWidth()
                    )
                }
                LoadingView(
                    isLoading = isLoadingState
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

@Composable
fun CodiItems(modifier: Modifier) {
    Box(
        modifier = modifier
    ) {

    }
}