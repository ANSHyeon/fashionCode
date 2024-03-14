package com.anshyeon.fashioncode.ui.screen.home.community.home

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.ui.graph.DetailHomeScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
) : ViewModel() {

    fun navigateCommunityWrite(navController: NavHostController) {
        navController.navigate(DetailHomeScreen.CommunityWrite.route)
    }

}