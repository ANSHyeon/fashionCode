package com.anshyeon.fashioncode.ui.screen.home.bookmark

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.ui.component.appBar.DefaultAppBar
import com.anshyeon.fashioncode.ui.component.calendar.HorizontalCalendar
import com.anshyeon.fashioncode.ui.component.loadingView.LoadingView
import com.anshyeon.fashioncode.ui.component.snackBar.TextSnackBarContainer

@Composable
fun CalendarScreen(navController: NavHostController) {

    val viewModel: CalendarViewModel = hiltViewModel()

    val appBarTitleState by viewModel.appBarTitle.collectAsStateWithLifecycle()
    val selectedDateState by viewModel.selectedDate.collectAsStateWithLifecycle()
    val isLoadingState by viewModel.isLoading.collectAsStateWithLifecycle()
    val snackBarTextState by viewModel.snackBarText.collectAsStateWithLifecycle()
    val showSnackBarState by viewModel.showSnackBar.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            DefaultAppBar(appBarTitleState) { }
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
                HorizontalCalendar(
                    selectedDate = selectedDateState,
                    onChangeAppBarTitle = { viewModel.onChangeAppBarTitle(it) }) {
                    viewModel.onChangeSelectedDate(
                        it
                    )
                }
                LoadingView(
                    isLoading = isLoadingState
                )
            }
        }
    }
}