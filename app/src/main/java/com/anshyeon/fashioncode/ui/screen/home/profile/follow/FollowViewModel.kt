package com.anshyeon.fashioncode.ui.screen.home.profile.follow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.data.model.Follow
import com.anshyeon.fashioncode.data.repository.AuthRepository
import com.anshyeon.fashioncode.ui.graph.DetailHomeScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    val myUserId: String = authRepository.getUserId()

    private val _followerList = MutableStateFlow<List<Follow>>(emptyList())
    var followerList: StateFlow<List<Follow>> = _followerList

    private val _followingList = MutableStateFlow<List<Follow>>(emptyList())
    var followingList: StateFlow<List<Follow>> = _followingList

    private val _myFollowingList = MutableStateFlow<List<Follow>>(emptyList())
    var myFollowingList: StateFlow<List<Follow>> = _myFollowingList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isGetFollowerLoading = MutableStateFlow(false)
    val isGetFollowerLoading: StateFlow<Boolean> = _isGetFollowerLoading

    private val _isGetFollowingLoading = MutableStateFlow(false)
    val isGetFollowingLoading: StateFlow<Boolean> = _isGetFollowingLoading

    private val _isGetMyFollowingLoading = MutableStateFlow(false)
    val isGetMyFollowingLoading: StateFlow<Boolean> = _isGetMyFollowingLoading

    private val _isGetFollowerComplete = MutableStateFlow(false)
    val isGetFollowerComplete: StateFlow<Boolean> = _isGetFollowerComplete

    private val _isGetFollowingComplete = MutableStateFlow(false)
    val isGetFollowingComplete: StateFlow<Boolean> = _isGetFollowingComplete

    private val _isGetMyFollowingComplete = MutableStateFlow(false)
    val isGetMyFollowingComplete: StateFlow<Boolean> = _isGetMyFollowingComplete

    private val _snackBarText = MutableStateFlow("")
    val snackBarText: StateFlow<String> = _snackBarText

    private val _showSnackBar = MutableStateFlow(false)
    val showSnackBar: StateFlow<Boolean> = _showSnackBar


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

    fun getMyFollowing() {
        _isGetMyFollowingLoading.value = true
        viewModelScope.launch {
            val response = authRepository.getFollowingList(
                myUserId,
                onComplete = {
                    _isGetMyFollowingLoading.value = false
                    _isGetMyFollowingComplete.value = true
                },
                onError = {
                    _showSnackBar.value = true
                    _snackBarText.value = "잠시 후 다시 시도해 주십시오"
                }
            )
            response.collectLatest {
                _myFollowingList.value = it
            }
        }
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
        _myFollowingList.value = _myFollowingList.value.toMutableList().apply {
            add(Follow("", myUserId, followingUserId ?: ""))
        }
    }

    fun removeFollower(followingUserId: String?) {
        _myFollowingList.value = _myFollowingList.value.toMutableList().apply {
            removeIf {
                it.following == followingUserId
            }
        }
    }

    fun dismissSnackBar() {
        _showSnackBar.value = false
    }

    fun navigateBack(navController: NavHostController) {
        navController.popBackStack()
    }

    fun navigateOtherUserProfile(navController: NavHostController, userId: String?) {
        navController.navigate("${DetailHomeScreen.OtherProfile.route}/${userId}")
    }
}