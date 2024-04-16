package com.anshyeon.fashioncode.ui.screen.home.profile.profileEdit

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.data.model.User
import com.anshyeon.fashioncode.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _nickName = MutableStateFlow("")
    val nickName: StateFlow<String> = _nickName

    private val _imageUrl = MutableStateFlow<String?>(null)
    val imageUrl: StateFlow<String?> = _imageUrl

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri

    private val _user = MutableStateFlow<User?>(null)
    var user: StateFlow<User?> = _user

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isGetUserLoading = MutableStateFlow(false)
    val isGetUserLoading: StateFlow<Boolean> = _isGetUserLoading

    private val _isGetUserComplete = MutableStateFlow(false)
    val isGetUserComplete: StateFlow<Boolean> = _isGetUserComplete

    private val _snackBarText = MutableStateFlow("")
    val snackBarText: StateFlow<String> = _snackBarText

    private val _showSnackBar = MutableStateFlow(false)
    val showSnackBar: StateFlow<Boolean> = _showSnackBar

    init {
        getUser()
    }

    private fun getUser() {
        _isGetUserLoading.value = true
        viewModelScope.launch {
            val response = authRepository.getUserInfo(
                authRepository.getUserId(),
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
                changeNickName(user.nickName)
                _imageUrl.value = user.profileUrl
                _user.value = user
            }
        }
    }

    fun changeNickName(newNickName: String) {
        _nickName.value = newNickName
    }

    fun changeImageUri(uri: Uri?) {
        _imageUri.value = uri
    }

    fun dismissSnackBar() {
        _showSnackBar.value = false
    }

    fun navigateBack(navController: NavHostController) {
        navController.popBackStack()
    }
}