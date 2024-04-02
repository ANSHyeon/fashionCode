package com.anshyeon.fashioncode.ui.screen.home.style.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.data.model.Style
import com.anshyeon.fashioncode.data.model.User
import com.anshyeon.fashioncode.data.repository.AuthRepository
import com.anshyeon.fashioncode.data.repository.StyleRepository
import com.anshyeon.fashioncode.network.extentions.onSuccess
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
class StyleDetailViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val styleRepository: StyleRepository
) : ViewModel() {

    private val _userId = MutableStateFlow("")
    val userId: StateFlow<String> = _userId

    private val _user = MutableStateFlow<User?>(null)
    var user: StateFlow<User?> = _user

    private val _styleList = MutableStateFlow<List<Style>>(emptyList())
    var styleList: StateFlow<List<Style>> = _styleList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isGetStyleListLoading = MutableStateFlow(false)
    val isGetStyleListLoading: StateFlow<Boolean> = _isGetStyleListLoading

    private val _isGetUserLoading = MutableStateFlow(false)
    val isGetUserLoading: StateFlow<Boolean> = _isGetUserLoading

    private val _isGetStyleListComplete = MutableStateFlow(false)
    val isGetStyleListComplete: StateFlow<Boolean> = _isGetStyleListComplete

    private val _isGetUserComplete = MutableStateFlow(false)
    val isGetUserComplete: StateFlow<Boolean> = _isGetUserComplete

    private val _snackBarText = MutableStateFlow("")
    val snackBarText: StateFlow<String> = _snackBarText

    private val _showSnackBar = MutableStateFlow(false)
    val showSnackBar: StateFlow<Boolean> = _showSnackBar

    init {
        _userId.value = authRepository.getUserId()
    }

    fun getStyleList(userId: String) {
        _isGetStyleListLoading.value = true
        viewModelScope.launch {
            transformReplyList(userId)
                .map {
                    val tempLikeList = MutableStateFlow<List<String>>(emptyList())
                    it.map { style ->
                        tempLikeList.value = emptyList()
                        val response = styleRepository.getStyleLikeList(
                            style.styleId,
                            {},
                            {}
                        )
                        response.collectLatest { likeList ->
                            tempLikeList.value = likeList
                        }
                        style.copy(
                            likeList = tempLikeList.value
                        )
                    }
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
            response.collectLatest {
                it.onSuccess { user ->
                    _user.value = user
                }
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

    fun dismissSnackBar() {
        _showSnackBar.value = false
    }

    fun navigateBack(navController: NavHostController) {
        navController.popBackStack()
    }
}