package com.anshyeon.fashioncode.ui.graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anshyeon.fashioncode.ui.screen.signin.info.InfoInputScreen
import com.anshyeon.fashioncode.ui.screen.signin.launch.SignInScreen
import com.anshyeon.fashioncode.util.Constants

@Composable
fun AuthNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = AuthScreen.SignIn.route,
        route = Constants.AUTH_GRAPH
    ) {
        composable(route = AuthScreen.SignIn.route) {
            SignInScreen(navController)
        }
        composable(route = AuthScreen.InfoInput.route) {
            InfoInputScreen()
        }
    }
}

sealed class AuthScreen(val route: String) {
    object SignIn : AuthScreen(route = "SIGN_IN")
    object InfoInput : AuthScreen(route = "INFO_INPUT")
}