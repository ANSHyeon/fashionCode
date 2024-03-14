package com.anshyeon.fashioncode.ui.screen.signin.info

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshyeon.fashioncode.MainActivity
import com.anshyeon.fashioncode.data.repository.AuthRepository
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
class InfoInputViewModel @Inject constructor(private val authRepository: AuthRepository) :
    ViewModel() {

    private val _nickName = MutableStateFlow("")
    val nickName: StateFlow<String> = _nickName

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri

    fun changeNickName(newNickName: String) {
        _nickName.value = newNickName
    }

    fun changeImageUri(uri: Uri?) {
        _imageUri.value = uri
    }

    fun saveUserInfo(context: Context) {
        viewModelScope.launch {
            val result = authRepository.createUser(nickName.value, imageUri.value)
            result.onSuccess {
                val saveIdToken = async { authRepository.saveIdToken() }
                saveIdToken.await()
                val intent = Intent(context, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)

            }.onError { code, message ->
            }.onException {
            }
        }
    }
}