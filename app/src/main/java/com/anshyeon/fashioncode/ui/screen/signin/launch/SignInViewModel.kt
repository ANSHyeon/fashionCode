package com.anshyeon.fashioncode.ui.screen.signin.launch

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.anshyeon.fashioncode.MainActivity
import com.anshyeon.fashioncode.data.repository.AuthRepository
import com.anshyeon.fashioncode.network.extentions.onError
import com.anshyeon.fashioncode.network.extentions.onException
import com.anshyeon.fashioncode.network.extentions.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    fun getUserInfo(context: Context, navController: NavController) {
        viewModelScope.launch {
            val result = repository.getUser()
            result.onSuccess {
                if (it.values.isNotEmpty()) {
                    saveUserInfo(context)
                } else {
                    navController.navigate("InfoInput") {
                        popUpTo("SignIn") {
                            inclusive = true
                        }
                    }
                }
            }.onError { _, message ->
            }.onException {
            }
        }
    }

    private suspend fun saveUserInfo(context: Context) {
        val getSaveIdToken = viewModelScope.async {
            repository.saveIdToken()
        }
        getSaveIdToken.await()
        context.startActivity(Intent(context, MainActivity::class.java))
    }
}