package com.anshyeon.fashioncode.ui.screen.home.profile.other

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.data.model.Follow
import com.anshyeon.fashioncode.data.model.Style
import com.anshyeon.fashioncode.data.model.User
import com.anshyeon.fashioncode.data.repository.AuthRepository
import com.anshyeon.fashioncode.data.repository.StyleRepository
import com.anshyeon.fashioncode.ui.graph.DetailHomeScreen
import com.anshyeon.fashioncode.util.SerializationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class OtherProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val styleRepository: StyleRepository
) : ViewModel() {

    val myUserId: String = authRepository.getUserId()

    private val _user = MutableStateFlow<User?>(null)
    var user: StateFlow<User?> = _user

    private val _styleList = MutableStateFlow<List<Style>>(emptyList())
    var styleList: StateFlow<List<Style>> = _styleList

    private val _followerList = MutableStateFlow<List<Follow>>(emptyList())
    var followerList: StateFlow<List<Follow>> = _followerList

    private val _followingList = MutableStateFlow<List<Follow>>(emptyList())
    var followingList: StateFlow<List<Follow>> = _followingList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isGetStyleListLoading = MutableStateFlow(false)
    val isGetStyleListLoading: StateFlow<Boolean> = _isGetStyleListLoading

    private val _isGetUserLoading = MutableStateFlow(false)
    val isGetUserLoading: StateFlow<Boolean> = _isGetUserLoading

    private val _isGetFollowerLoading = MutableStateFlow(false)
    val isGetFollowerLoading: StateFlow<Boolean> = _isGetFollowerLoading

    private val _isGetFollowingLoading = MutableStateFlow(false)
    val isGetFollowingLoading: StateFlow<Boolean> = _isGetFollowingLoading

    private val _isGetStyleListComplete = MutableStateFlow(false)
    val isGetStyleListComplete: StateFlow<Boolean> = _isGetStyleListComplete

    private val _isGetUserComplete = MutableStateFlow(false)
    val isGetUserComplete: StateFlow<Boolean> = _isGetUserComplete

    private val _isGetFollowerComplete = MutableStateFlow(false)
    val isGetFollowerComplete: StateFlow<Boolean> = _isGetFollowerComplete

    private val _isGetFollowingComplete = MutableStateFlow(false)
    val isGetFollowingComplete: StateFlow<Boolean> = _isGetFollowingComplete

    private val _snackBarText = MutableStateFlow("")
    val snackBarText: StateFlow<String> = _snackBarText

    private val _showSnackBar = MutableStateFlow(false)
    val showSnackBar: StateFlow<Boolean> = _showSnackBar

    fun getStyleList(userId: String) {
        _isGetStyleListLoading.value = true
        viewModelScope.launch {
            transformReplyList(userId)
                .map {
                    val styleListWithLikes = viewModelScope.async {
                        it.map { style ->
                            viewModelScope.async {
                                val tempLikeList = mutableListOf<String>()
                                val response = styleRepository.getStyleLikeList(
                                    style.styleId,
                                    {},
                                    {}
                                )
                                response.collectLatest { likeList ->
                                    tempLikeList.addAll(likeList)
                                }
                                style.copy(
                                    isLike = tempLikeList.any { it == myUserId },
                                    likeCount = tempLikeList.size,
                                    likeList = tempLikeList.toList()
                                )
                            }
                        }
                    }
                    styleListWithLikes.await().map { it.await() }
                }
                .onCompletion {
                    _isGetStyleListLoading.value = false
                    _isGetStyleListComplete.value = true
                }.collectLatest {
                    _styleList.value = it
                }
        }
    }

    private fun transformReplyList(userId: String): Flow<List<Style>> {
        return styleRepository.getStyleListWithWriter(
            userId,
            onComplete = { },
            onError = {
                _showSnackBar.value = true
                _snackBarText.value = "잠시 후 다시 시도해 주십시오"
            }
        ).map {
            it.sortedBy { reply -> reply.createdDate }
        }
    }

    fun getUser(userId: String) {
        _isGetUserLoading.value = true
        viewModelScope.launch {
            val response = authRepository.getUserInfo(
                userId,
                onComplete = {
                    _isGetUserLoading.value = false
                    _isGetUserComplete.value = true
                },
                onError = {
                    _showSnackBar.value = true
                    _snackBarText.value = "잠시 후 다시 시도해 주십시오"
                }
            )
            response.collectLatest { user ->
                _user.value = user
            }
        }
    }

    fun getFollower(userId: String) {
        _isGetFollowerLoading.value = true
        viewModelScope.launch {
            val response = authRepository.getFollowerList(
                userId,
                onComplete = {
                    _isGetFollowerLoading.value = false
                    _isGetFollowerComplete.value = true
                },
                onError = {
                    _showSnackBar.value = true
                    _snackBarText.value = "잠시 후 다시 시도해 주십시오"
                }
            )
            response.collectLatest {
                _followerList.value = it
            }
        }
    }

    fun getFollowing(userId: String) {
        _isGetFollowingLoading.value = true
        viewModelScope.launch {
            val response = authRepository.getFollowingList(
                userId,
                onComplete = {
                    _isGetFollowingLoading.value = false
                    _isGetFollowingComplete.value = true
                },
                onError = {
                    _showSnackBar.value = true
                    _snackBarText.value = "잠시 후 다시 시도해 주십시오"
                }
            )
            response.collectLatest {
                _followingList.value = it
            }
        }
    }

    fun createLike(styleId: String) {
        viewModelScope.launch {
            styleRepository.createLike(
                styleId
            )
        }
    }

    fun deleteLike(styleId: String) {
        viewModelScope.launch {
            styleRepository.deleteLike(
                styleId
            )
        }
    }

    fun setStyleLike(index: Int, isCheck: Boolean, count: Int) {
        val tempList = _styleList.value.toMutableList()
        tempList[index] = tempList[index].copy(
            isLike = isCheck,
            likeCount = count
        )
        _styleList.value = tempList.toList()
    }

    fun createFollow(followingUserId: String?) {
        viewModelScope.launch {
            authRepository.createFollow(
                followingUserId ?: ""
            )
        }
    }

    fun deleteFollow(followingUserId: String?) {
        viewModelScope.launch {
            authRepository.deleteFollow(
                followingUserId ?: ""
            )
        }
    }

    fun addFollower(followingUserId: String?) {
        _followerList.value = _followerList.value.toMutableList().apply {
            add(Follow("", myUserId, followingUserId ?: ""))
        }
    }

    fun removeFollower() {
        _followerList.value = _followerList.value.toMutableList().apply {
            removeIf {
                it.follower == myUserId
            }
        }
    }

    fun dismissSnackBar() {
        _showSnackBar.value = false
    }

    fun navigateBack(navController: NavHostController) {
        navController.popBackStack()
    }

    fun navigateStyleDetail(
        navController: NavHostController,
        style: Style,
    ) {
        val styleJson = SerializationUtils.toJson(style)
        val encodedStyleUrl = URLEncoder.encode(styleJson, StandardCharsets.UTF_8.toString())
        navController.navigate("${DetailHomeScreen.StyleDetail.route}/${encodedStyleUrl}")
    }

    fun navigateFollow(navController: NavHostController, userId: String?) {
        navController.navigate("${DetailHomeScreen.Follow.route}/${userId}")
    }
}