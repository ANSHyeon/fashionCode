package com.anshyeon.fashioncode.ui.screen.home.style.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.R
import com.anshyeon.fashioncode.ui.component.appBar.DefaultAppBar
import com.anshyeon.fashioncode.ui.component.loadingView.LoadingView
import com.anshyeon.fashioncode.ui.component.snackBar.TextSnackBarContainer

@Composable
fun StyleScreen(navController: NavHostController) {

    val viewModel: StyleViewModel = hiltViewModel()

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
            LoadingView(
                isLoading = isLoadingState
            )
        }
    }
}
