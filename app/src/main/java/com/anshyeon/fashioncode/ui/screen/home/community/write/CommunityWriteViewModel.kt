package com.anshyeon.fashioncode.ui.screen.home.community.write

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.data.repository.AuthRepository
import com.anshyeon.fashioncode.data.repository.PostRepository
import com.anshyeon.fashioncode.network.extentions.onError
import com.anshyeon.fashioncode.network.extentions.onException
import com.anshyeon.fashioncode.network.extentions.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityWriteViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    private val _selectedImageList = MutableStateFlow(emptyList<Uri>())
    val selectedImageList: StateFlow<List<Uri>> = _selectedImageList

    private val _postTitle = MutableStateFlow("")
    val postTitle: StateFlow<String> = _postTitle

    private val _postBody = MutableStateFlow("")
    val postBody: StateFlow<String> = _postBody

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _snackBarText = MutableStateFlow("")
    val snackBarText: StateFlow<String> = _snackBarText

    private val _showSnackBar = MutableStateFlow(false)
    val showSnackBar: StateFlow<Boolean> = _showSnackBar

    fun changeTitle(newTitle: String) {
        _postTitle.value = newTitle
    }

    fun changeBody(newBody: String) {
        _postBody.value = newBody
    }

    fun addImageUris(imageUris: List<Uri>) {
        _selectedImageList.value = mutableListOf<Uri>().apply {
            addAll(_selectedImageList.value)
            addAll(imageUris)
        }
    }

    fun removeImageUris(index: Int) {
        _selectedImageList.value = _selectedImageList.value.toMutableList().apply {
            removeAt(index)
        }.toList()
    }

    fun submitPost(navController: NavHostController) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = postRepository.createPost(
                postTitle.value,
                postBody.value,
                authRepository.getUserId(),
                selectedImageList.value
            )
            result.onSuccess {
                _isLoading.value = false
                navigateBack(navController)
            }.onError { _, _ ->
                _isLoading.value = false
                _showSnackBar.value = true
                _snackBarText.value = "잠시 후 다시 시도해 주십시오"
            }.onException {
                _isLoading.value = false
                _showSnackBar.value = true
                _snackBarText.value = "잠시 후 다시 시도해 주십시오"
            }
        }
    }

    fun dismissSnackBar() {
        _showSnackBar.value = false
    }

    fun navigateBack(navController: NavHostController) {
        navController.popBackStack()
    }
}