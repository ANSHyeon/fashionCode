package com.anshyeon.fashioncode.ui.graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anshyeon.fashioncode.ui.screen.signin.info.InfoInputScreen
import com.anshyeon.fashioncode.ui.screen.signin.launch.SignInScreen

@Composable
fun AuthNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "SignIn",
        route = "AUTH_GRAPH"
    ) {
        composable(route = "SignIn") {
            SignInScreen(navController)
        }
        composable(route = "InfoInput") {
            InfoInputScreen()
        }
    }
}