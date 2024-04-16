package com.anshyeon.fashioncode.ui.screen.home.profile.profileEdit

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.R
import com.anshyeon.fashioncode.ui.component.appBar.BackButtonAppBar
import com.anshyeon.fashioncode.ui.component.button.RectangleButton
import com.anshyeon.fashioncode.ui.component.loadingView.LoadingView
import com.anshyeon.fashioncode.ui.component.snackBar.TextSnackBarContainer
import com.anshyeon.fashioncode.ui.screen.signin.info.SetUserInfo
import com.anshyeon.fashioncode.util.isValidNickname

@Composable
fun ProfileEditScreen(navController: NavHostController) {

    val viewModel: ProfileEditViewModel = hiltViewModel()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.changeImageUri(uri)
    }
    val nickNameState by viewModel.nickName.collectAsStateWithLifecycle()
    val imageUrlState by viewModel.imageUrl.collectAsStateWithLifecycle()
    val imageUriState by viewModel.imageUri.collectAsStateWithLifecycle()
    val nextButtonVisibility = isValidNickname(nickNameState)
    val isLoadingState by viewModel.isLoading.collectAsStateWithLifecycle()
    val isGetUserLoadingState by viewModel.isGetUserLoading.collectAsStateWithLifecycle()
    val isGetUserCompleteState by viewModel.isGetUserComplete.collectAsStateWithLifecycle()
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
            if (isGetUserCompleteState) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    val image = if (imageUriState == null) {
                        imageUrlState?.toUri()
                    } else {
                        imageUriState
                    }
                    SetUserInfo(image, nickNameState, launcher) { newNickName ->
                        viewModel.changeNickName(newNickName)
                    }
                    RectangleButton(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        text = stringResource(id = R.string.label_start),
                        visibility = nextButtonVisibility
                    ) { }
                }
            }

            LoadingView(
                isLoading = isLoadingState || isGetUserLoadingState
            )
        }
    }
}