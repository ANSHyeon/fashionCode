package com.anshyeon.fashioncode.ui.screen.home.community.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.R
import com.anshyeon.fashioncode.ui.component.appBar.DefaultAppBar

@Composable
fun CommunityScreen(navController: NavHostController) {

    val viewModel: CommunityViewModel = hiltViewModel()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            DefaultAppBar(stringResource(id = R.string.label_app_bar_community)) {
                IconButton(
                    onClick = { viewModel.navigateCommunityWrite(navController) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Icon",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}
