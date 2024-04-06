package com.anshyeon.fashioncode.ui.screen.home.profile.me

import androidx.compose.foundation.layout.Box
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
import com.anshyeon.fashioncode.ui.component.appBar.DefaultAppBar
import com.anshyeon.fashioncode.ui.component.loadingView.LoadingView
import com.anshyeon.fashioncode.ui.component.snackBar.TextSnackBarContainer

@Composable
fun ProfileScreen(navController: NavHostController) {

    val viewModel: ProfileViewModel = hiltViewModel()

    val isLoadingState by viewModel.isLoading.collectAsStateWithLifecycle()
    val snackBarTextState by viewModel.snackBarText.collectAsStateWithLifecycle()
    val showSnackBarState by viewModel.showSnackBar.collectAsStateWithLifecycle()

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

            Box(modifier = Modifier.padding(it))

            LoadingView(
                isLoading = isLoadingState
            )
        }
    }
}