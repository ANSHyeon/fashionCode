package com.anshyeon.fashioncode.ui.screen.signin.launch

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
) : ViewModel() {
    private val _loginResult = MutableStateFlow<Boolean>(true)
    var loginResult = _loginResult.asStateFlow()

    fun tryLogin(context: Context) {
        viewModelScope.launch {
            val account = async {
                getLastSignedInAccount(context)
            }
            setLoginResult(account.await() != null)
        }
    }

    private fun getLastSignedInAccount(context: Context) =
        GoogleSignIn.getLastSignedInAccount(context)

    private fun setLoginResult(isLogin: Boolean) {
        viewModelScope.launch {
            _loginResult.emit(isLogin)
        }
    }
}