package com.anshyeon.fashioncode.ui.graph

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.anshyeon.fashioncode.R
import com.anshyeon.fashioncode.data.model.Comment
import com.anshyeon.fashioncode.data.model.Style
import com.anshyeon.fashioncode.data.model.User
import com.anshyeon.fashioncode.ui.screen.home.style.home.StyleScreen
import com.anshyeon.fashioncode.ui.screen.home.community.home.CommunityScreen
import com.anshyeon.fashioncode.ui.screen.home.bookmark.CalendarScreen
import com.anshyeon.fashioncode.ui.screen.home.community.detail.CommunityDetailScreen
import com.anshyeon.fashioncode.ui.screen.home.community.reply.CommunityReplyScreen
import com.anshyeon.fashioncode.ui.screen.home.community.write.CommunityWriteScreen
import com.anshyeon.fashioncode.ui.screen.home.profile.follow.FollowScreen
import com.anshyeon.fashioncode.ui.screen.home.profile.me.ProfileScreen
import com.anshyeon.fashioncode.ui.screen.home.profile.other.OtherProfileScreen
import com.anshyeon.fashioncode.ui.screen.home.profile.profileEdit.ProfileEditScreen
import com.anshyeon.fashioncode.ui.screen.home.style.create.StyleCreateScreen
import com.anshyeon.fashioncode.ui.screen.home.style.detail.StyleDetailScreen
import com.anshyeon.fashioncode.util.Constants
import com.anshyeon.fashioncode.util.SerializationUtils
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun HomeNavGraph(navController: NavHostController, userList: List<User>) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Style.route,
        route = Constants.HOME_GRAPH
    ) {
        composable(route = BottomNavItem.Style.route) {
            StyleScreen(navController, userList)
        }
        composable(route = BottomNavItem.Calendar.route) {
            CalendarScreen(navController)
        }
        composable(route = BottomNavItem.Community.route) {
            CommunityScreen(navController)
        }
        composable(route = BottomNavItem.Setting.route) {
            ProfileScreen(navController)
        }
        composable(route = DetailHomeScreen.CommunityWrite.route) {
            CommunityWriteScreen(navController)
        }
        composable(
            route = DetailHomeScreen.CommunityDetail.routeWithArgName(),
            arguments = DetailHomeScreen.CommunityDetail.arguments
        ) { navBackStackEntry ->
            val postId = navBackStackEntry.arguments?.getString("postId").toString()
            CommunityDetailScreen(navController, userList, postId)
        }
        composable(
            route = DetailHomeScreen.CommunityReply.routeWithArgName(),
            arguments = DetailHomeScreen.CommunityReply.arguments
        ) { navBackStackEntry ->
            val encodedCommentJson = navBackStackEntry.arguments?.getString("comment").toString()
            val commentJson =
                URLDecoder.decode(encodedCommentJson, StandardCharsets.UTF_8.toString())

            CommunityReplyScreen(navController, SerializationUtils.fromJson<Comment>(commentJson)!!)
        }
        composable(
            route = DetailHomeScreen.StyleCreate.routeWithArgName(),
            arguments = DetailHomeScreen.StyleCreate.arguments
        ) { navBackStackEntry ->
            val selectedDate = navBackStackEntry.arguments?.getString("selectedDate").toString()
            StyleCreateScreen(navController, selectedDate)
        }
        composable(
            route = DetailHomeScreen.StyleDetail.routeWithArgName(),
            arguments = DetailHomeScreen.StyleDetail.arguments
        ) { navBackStackEntry ->
            val encodedStyleJson = navBackStackEntry.arguments?.getString("style").toString()
            val styleJson =
                URLDecoder.decode(encodedStyleJson, StandardCharsets.UTF_8.toString())

            StyleDetailScreen(
                navController,
                userList,
                SerializationUtils.fromJson<Style>(styleJson)!!,
            )
        }
        composable(
            route = DetailHomeScreen.OtherProfile.routeWithArgName(),
            arguments = DetailHomeScreen.OtherProfile.arguments
        ) { navBackStackEntry ->
            val userId = navBackStackEntry.arguments?.getString("userId").toString()
            OtherProfileScreen(navController, userList, userId)
        }
        composable(
            route = DetailHomeScreen.Follow.routeWithArgName(),
            arguments = DetailHomeScreen.Follow.arguments
        ) { navBackStackEntry ->
            val userId = navBackStackEntry.arguments?.getString("userId").toString()
            FollowScreen(navController, userId)
        }
        composable(route = DetailHomeScreen.ProfileEdit.route) {
            ProfileEditScreen(navController)
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

    object Calendar : BottomNavItem(
        route = "CALENDAR",
        title = "캘린더",
        icon = R.drawable.ic_calendar,
        outlinedIcon = R.drawable.ic_calendar_outline,
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

sealed class DetailHomeScreen(val route: String, val argName: String) {
    object CommunityWrite : DetailHomeScreen(route = "COMMUNITY_WRITE", argName = "")
    object CommunityDetail : DetailHomeScreen(route = "COMMUNITY_DETAIL", argName = "postId")
    object CommunityReply : DetailHomeScreen(route = "COMMUNITY_REPLY", argName = "comment")
    object StyleCreate : DetailHomeScreen(route = "STYLE_CREATE", argName = "selectedDate")
    object StyleDetail : DetailHomeScreen(route = "STYLE_DETAIL", argName = "style")
    object OtherProfile : DetailHomeScreen(route = "OTHER_PROFILE", argName = "userId")
    object Follow : DetailHomeScreen(route = "FOLLOW", argName = "userId")
    object ProfileEdit : DetailHomeScreen(route = "PROFILE_EDIT", argName = "")

    val arguments: List<NamedNavArgument> = listOf(
        navArgument(argName) { type = NavType.StringType }
    )

    fun routeWithArgName(): String {
        return "$route/{$argName}"
    }
}