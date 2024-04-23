package com.anshyeon.fashioncode.ui.screen.signin.launch

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.anshyeon.fashioncode.MainActivity
import com.anshyeon.fashioncode.data.repository.AuthRepository
import com.anshyeon.fashioncode.data.repository.TokenRepository
import com.anshyeon.fashioncode.network.extentions.onError
import com.anshyeon.fashioncode.network.extentions.onException
import com.anshyeon.fashioncode.network.extentions.onSuccess
import com.anshyeon.fashioncode.ui.graph.AuthScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _snackBarText = MutableStateFlow("")
    val snackBarText: StateFlow<String> = _snackBarText

    private val _showSnackBar = MutableStateFlow(false)
    val showSnackBar: StateFlow<Boolean> = _showSnackBar

    fun getUserInfo(context: Context, navController: NavController) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.getUser()
            result.onSuccess {
                if (it.values.isNotEmpty()) {
                    val user = it.values.first()
                    saveUserInfo(context, user.nickName, user.profileUrl)
                } else {
                    navController.navigate(AuthScreen.InfoInput.route) {
                        popUpTo(AuthScreen.SignIn.route) {
                            inclusive = true
                        }
                    }
                }
            }.onError { _, message ->
                _showSnackBar.value = true
                _snackBarText.value = "잠시 후 다시 시도해 주십시오"
            }.onException {
                _showSnackBar.value = true
                _snackBarText.value = "잠시 후 다시 시도해 주십시오"
            }
            _isLoading.value = false
        }
    }

    private suspend fun saveUserInfo(context: Context, nickName: String, url: String?) {
        val saveIdTokenJob = viewModelScope.async {
            authRepository.saveUserInfo(nickName, url)
        }

        val getDropBoxTokenJob = viewModelScope.async {
            val token = tokenRepository.getDropBoxRefreshToken()
            tokenRepository.saveDropBoxToken(token)
        }
        val getAdobeTokenJob = viewModelScope.async {
            val token = tokenRepository.getAdobeRefreshToken()
            tokenRepository.saveAdobeToken("zzzzzzzzzzzzzzzzzzzzzzzzzz")
        }
        getDropBoxTokenJob.await()
        getAdobeTokenJob.await()
        saveIdTokenJob.await()

        context.startActivity(Intent(context, MainActivity::class.java))
    }

    fun dismissSnackBar() {
        _showSnackBar.value = false
    }
}