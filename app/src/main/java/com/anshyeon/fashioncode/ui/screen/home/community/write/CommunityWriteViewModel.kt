package com.anshyeon.fashioncode.ui.screen.home.community.write

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommunityWriteViewModel @Inject constructor(
) : ViewModel() {

    fun navigateBack(navController: NavHostController) {
        navController.popBackStack()
    }
}