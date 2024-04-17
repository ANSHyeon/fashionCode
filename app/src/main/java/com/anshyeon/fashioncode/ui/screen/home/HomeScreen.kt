package com.anshyeon.fashioncode.ui.screen.home

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.anshyeon.fashioncode.SignInActivity
import com.anshyeon.fashioncode.ui.graph.BottomNavItem
import com.anshyeon.fashioncode.ui.graph.HomeNavGraph

@Composable
fun HomeScreen(navController: NavHostController = rememberNavController()) {

    val viewModel: HomeViewModel = hiltViewModel()
    val localGoogleIdToken = viewModel.getLocalGoogleIdToken()

    val userListState by viewModel.userList.collectAsStateWithLifecycle(emptyList())

    if (localGoogleIdToken.isEmpty()) {
        with(LocalContext.current) {
            val intent = Intent(this, SignInActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
    }

    Scaffold(
        bottomBar = { BottomBar(navController = navController) }
    ) {
        Box(Modifier.padding(it)) {
            HomeNavGraph(
                navController = navController,
                userList = userListState
            )
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomNavItem.Style,
        BottomNavItem.Calendar,
        BottomNavItem.Community,
        BottomNavItem.Setting,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isBottomBarDestination = screens.any { it.route == currentRoute }
    if (isBottomBarDestination) {
        BottomNavigation(
            backgroundColor = Color.White,
            contentColor = Color.Black
        ) {
            screens.forEach { screen ->
                val selected = currentRoute == screen.route
                AddItem(
                    screen = screen,
                    navController = navController,
                    selected = selected
                )
            }
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomNavItem,
    navController: NavHostController,
    selected: Boolean
) {
    BottomNavigationItem(
        icon = {
            Icon(
                painter = painterResource(id = if (selected) screen.icon else screen.outlinedIcon),
                contentDescription = screen.title,
                tint = Color.Black
            )
        },
        label = {
            Text(screen.title, fontWeight = if (selected) FontWeight.Bold else FontWeight.Light)
        },
        selected = selected,
        onClick = {
            navController.navigate(screen.route) {
                navController.graph.startDestinationRoute?.let {
                    popUpTo(it) {
                        saveState = true
                    }
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}