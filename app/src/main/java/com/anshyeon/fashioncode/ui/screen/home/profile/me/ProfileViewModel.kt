package com.anshyeon.fashioncode.ui.screen.home.profile.me

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.data.model.Follow
import com.anshyeon.fashioncode.data.model.Style
import com.anshyeon.fashioncode.data.model.User
import com.anshyeon.fashioncode.data.repository.AuthRepository
import com.anshyeon.fashioncode.data.repository.StyleRepository
import com.anshyeon.fashioncode.ui.graph.DetailHomeScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
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

    init {
        getUser(myUserId)
        getFollower(myUserId)
        getFollowing(myUserId)
        getStyleList(myUserId)
    }

    private fun getStyleList(userId: String) {
        _isGetStyleListLoading.value = true
        viewModelScope.launch {
            transformStyleList(userId)
                .onCompletion {
                    _isGetStyleListLoading.value = false
                    _isGetStyleListComplete.value = true
                }.collectLatest {
                    _styleList.value = it
                }
        }
    }

    private fun transformStyleList(userId: String): Flow<List<Style>> {
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

    private fun getUser(userId: String) {
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

    private fun getFollower(userId: String) {
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

    private fun getFollowing(userId: String) {
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

    fun dismissSnackBar() {
        _showSnackBar.value = false
    }

    fun navigateBack(navController: NavHostController) {
        navController.popBackStack()
    }

    fun navigateFollow(navController: NavHostController) {
        navController.navigate("${DetailHomeScreen.Follow.route}/${myUserId}")
    }
}