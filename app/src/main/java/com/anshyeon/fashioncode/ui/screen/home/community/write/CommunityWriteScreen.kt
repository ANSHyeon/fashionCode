package com.anshyeon.fashioncode.ui.screen.home.community.write

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.R
import com.anshyeon.fashioncode.ui.component.appBar.BackButtonAppBar

@Composable
fun CommunityWriteScreen(navController: NavHostController) {

    val viewModel: CommunityWriteViewModel = hiltViewModel()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            BackButtonAppBar(title = stringResource(id = R.string.label_app_bar_community_write)) {
                viewModel.navigateBack(navController)
            }
        }
    }
}
