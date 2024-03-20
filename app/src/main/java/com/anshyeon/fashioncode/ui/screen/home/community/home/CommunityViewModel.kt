package com.anshyeon.fashioncode.ui.screen.home.community.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.data.model.Post
import com.anshyeon.fashioncode.data.repository.PostRepository
import com.anshyeon.fashioncode.ui.graph.DetailHomeScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _snackBarText = MutableStateFlow("")
    val snackBarText: StateFlow<String> = _snackBarText

    private val _showSnackBar = MutableStateFlow(false)
    val showSnackBar: StateFlow<Boolean> = _showSnackBar

    val postList = transformPostList().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(500),
        initialValue = emptyList()
    )

    private fun transformPostList(): Flow<List<Post>> {
        _isLoading.value = true
        return postRepository.getPostList(
            onComplete = { _isLoading.value = false },
            onError = {
                _showSnackBar.value = true
                _snackBarText.value = "잠시 후 다시 시도해 주십시오"
            }
        ).map {
            it.sortedByDescending { post -> post.createdDate }
        }
    }

    fun dismissSnackBar() {
        _showSnackBar.value = false
    }

    fun navigateCommunityWrite(navController: NavHostController) {
        navController.navigate(DetailHomeScreen.CommunityWrite.route)
    }

    fun navigateCommunityDetail(navController: NavHostController, post: Post) {
        navController.navigate("${DetailHomeScreen.CommunityDetail.route}/${post.postId}")
    }
}