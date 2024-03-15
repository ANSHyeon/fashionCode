package com.anshyeon.fashioncode.ui.graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.anshyeon.fashioncode.R
import com.anshyeon.fashioncode.ui.screen.home.style.StyleScreen
import com.anshyeon.fashioncode.ui.screen.home.community.home.CommunityScreen
import com.anshyeon.fashioncode.ui.screen.home.bookmark.BookMarkScreen
import com.anshyeon.fashioncode.ui.screen.home.community.write.CommunityWriteScreen
import com.anshyeon.fashioncode.ui.screen.home.setting.SettingScreen
import com.anshyeon.fashioncode.util.Constants

@Composable
fun HomeNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Style.route,
        route = Constants.HOME_GRAPH
    ) {
        composable(route = BottomNavItem.Style.route) {
            StyleScreen(navController)
        }
        composable(route = BottomNavItem.BookMark.route) {
            BookMarkScreen(navController)
        }
        composable(route = BottomNavItem.Community.route) {
            CommunityScreen(navController)
        }
        composable(route = BottomNavItem.Setting.route) {
            SettingScreen(navController)
        }
        composable(route = DetailHomeScreen.CommunityWrite.route) {
            CommunityWriteScreen(navController)
        }
    }
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: Int,
    val outlinedIcon: Int,
) {
    object Style : BottomNavItem(
        route = "STYLE",
        title = "스타일",
        icon = R.drawable.ic_style,
        outlinedIcon = R.drawable.ic_style_outline,
    )

    object BookMark : BottomNavItem(
        route = "BOOKMARK",
        title = "북마크",
        icon = R.drawable.ic_bookmark,
        outlinedIcon = R.drawable.ic_bookmark_outline,
    )

    object Community : BottomNavItem(
        route = "COMMUNITY",
        title = "게시판",
        icon = R.drawable.ic_community,
        outlinedIcon = R.drawable.ic_community_outline,
    )

    object Setting : BottomNavItem(
        route = "SETTING",
        title = "마이페이지",
        icon = R.drawable.ic_person,
        outlinedIcon = R.drawable.ic_person_outline,
    )
}

sealed class DetailHomeScreen(val route: String) {
    object CommunityWrite : DetailHomeScreen(route = "COMMUNITY_WRITE")
}