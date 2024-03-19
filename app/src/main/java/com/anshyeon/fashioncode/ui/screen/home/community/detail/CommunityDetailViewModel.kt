package com.anshyeon.fashioncode.ui.screen.home.community.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.data.model.Post
import com.anshyeon.fashioncode.data.model.User
import com.anshyeon.fashioncode.data.repository.AuthRepository
import com.anshyeon.fashioncode.data.repository.PostRepository
import com.anshyeon.fashioncode.network.extentions.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityDetailViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    var post: StateFlow<Post?> = _post

    private val _user = MutableStateFlow<User?>(null)
    var user: StateFlow<User?> = _user

    private val _commentBody = MutableStateFlow("")
    var commentBody: StateFlow<String> = _commentBody

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isGetComplete = MutableStateFlow(false)
    val isGetComplete: StateFlow<Boolean> = _isGetComplete

    private val _snackBarText = MutableStateFlow("")
    val snackBarText: StateFlow<String> = _snackBarText

    private val _showSnackBar = MutableStateFlow(false)
    val showSnackBar: StateFlow<Boolean> = _showSnackBar

    fun getPost(postId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val response = postRepository.getPost(
                postId,
                onComplete = {
                    _isLoading.value = false
                },
                onError = {
                    _showSnackBar.value = true
                    _snackBarText.value = "잠시 후 다시 시도해 주십시오"
                }
            )
            response.collectLatest {
                it.onSuccess { post ->
                    _post.value = post
                    getUser(post.writer)
                }
            }
        }
    }

    private fun getUser(userId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val response = authRepository.getUserInfo(
                userId,
                onComplete = {
                    _isLoading.value = false
                    _isGetComplete.value = true
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

    fun changeCommentBody(newBody: String) {
        _commentBody.value = newBody
    }

    fun dismissSnackBar() {
        _showSnackBar.value = false
    }

    fun navigateBack(navController: NavHostController) {
        navController.popBackStack()
    }
}