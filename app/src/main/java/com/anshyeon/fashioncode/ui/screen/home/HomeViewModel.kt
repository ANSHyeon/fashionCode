package com.anshyeon.fashioncode.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshyeon.fashioncode.data.model.User
import com.anshyeon.fashioncode.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    init {
        getUserList()
    }

    val userList = transformUserList().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(500),
        initialValue = emptyList()
    )

    private fun transformUserList(): Flow<List<User>> {
        return authRepository.getStyleListByRoom()
    }

    private fun getUserList() {
        viewModelScope.launch {
            authRepository.getUserList()
        }
    }

    fun getLocalGoogleIdToken(): String {
        return authRepository.getLocalIdToken()
    }
}