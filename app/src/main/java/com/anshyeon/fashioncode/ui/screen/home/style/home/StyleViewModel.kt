package com.anshyeon.fashioncode.ui.screen.home.style.home

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.ui.graph.DetailHomeScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class StyleViewModel @Inject constructor(
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _snackBarText = MutableStateFlow("")
    val snackBarText: StateFlow<String> = _snackBarText

    private val _showSnackBar = MutableStateFlow(false)
    val showSnackBar: StateFlow<Boolean> = _showSnackBar


    fun dismissSnackBar() {
        _showSnackBar.value = false
    }

    fun navigateStyleCreate(navController: NavHostController) {
        navController.navigate(DetailHomeScreen.StyleCreate.route)
    }
}