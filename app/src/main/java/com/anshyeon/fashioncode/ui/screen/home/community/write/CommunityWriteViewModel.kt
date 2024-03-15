package com.anshyeon.fashioncode.ui.screen.home.community.write

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.data.model.User
import com.anshyeon.fashioncode.data.repository.AuthRepository
import com.anshyeon.fashioncode.data.repository.PostRepository
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

    fun changeTitle(newTitle: String) {
        _postTitle.value = newTitle
    }

    fun changeBody(newBodye: String) {
        _postBody.value = newBodye
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
        viewModelScope.launch {
            val result = authRepository.getUser()
            result.onSuccess {
                val user = it.values.first()
                createPost(user, navController)

            }
        }
    }

    private fun createPost(user: User, navController: NavHostController) {
        viewModelScope.launch {
            val result = postRepository.createPost(
                postTitle.value,
                postBody.value,
                user,
                selectedImageList.value
            )
            result.onSuccess {
                navigateBack(navController)
            }
        }
    }

    fun navigateBack(navController: NavHostController) {
        navController.popBackStack()
    }
}