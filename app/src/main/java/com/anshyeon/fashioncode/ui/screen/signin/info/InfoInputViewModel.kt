package com.anshyeon.fashioncode.ui.screen.signin.info

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshyeon.fashioncode.MainActivity
import com.anshyeon.fashioncode.data.repository.AuthRepository
import com.anshyeon.fashioncode.data.repository.TokenRepository
import com.anshyeon.fashioncode.network.extentions.onError
import com.anshyeon.fashioncode.network.extentions.onException
import com.anshyeon.fashioncode.network.extentions.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InfoInputViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository,
) : ViewModel() {

    private val _nickName = MutableStateFlow("")
    val nickName: StateFlow<String> = _nickName

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _snackBarText = MutableStateFlow("")
    val snackBarText: StateFlow<String> = _snackBarText

    private val _showSnackBar = MutableStateFlow(false)
    val showSnackBar: StateFlow<Boolean> = _showSnackBar

    fun changeNickName(newNickName: String) {
        _nickName.value = newNickName
    }

    fun changeImageUri(uri: Uri?) {
        _imageUri.value = uri
    }

    fun saveUserInfo(context: Context) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.createUser(nickName.value, imageUri.value)
            result.onSuccess {
                val saveIdToken = async {
                    authRepository.saveUserInfo(nickName.value, null)
                }
                val saveImageTokenJob = viewModelScope.async {
                    val getDropBoxTokenJob = viewModelScope.async {
                        tokenRepository.getDropBoxToken()
                    }
                    val getAdobeTokenJob = viewModelScope.async {
                        tokenRepository.getAdobeLoginToken()
                    }
                    val dropboxToken = getDropBoxTokenJob.await()
                    val adobeToken = getAdobeTokenJob.await()
                    tokenRepository.saveImageToken(adobeToken, dropboxToken)
                }

                saveIdToken.await()
                saveImageTokenJob.await()
                val intent = Intent(context, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)

            }.onError { code, message ->
                _showSnackBar.value = true
                _snackBarText.value = "잠시 후 다시 시도해 주십시오"
            }.onException {
                _showSnackBar.value = true
                _snackBarText.value = "잠시 후 다시 시도해 주십시오"
            }
            _isLoading.value = false
        }
    }

    fun dismissSnackBar() {
        _showSnackBar.value = false
    }
}