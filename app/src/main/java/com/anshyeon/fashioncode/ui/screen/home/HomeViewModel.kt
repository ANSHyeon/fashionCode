package com.anshyeon.fashioncode.ui.screen.home

import androidx.lifecycle.ViewModel
import com.anshyeon.fashioncode.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun getLocalGoogleIdToken(): String {
        return authRepository.getLocalIdToken() ?: ""
    }
}