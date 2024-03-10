package com.anshyeon.fashioncode.ui.screen.signin.info

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class InfoInputViewModel @Inject constructor(
) : ViewModel() {

    private val _nickName = MutableStateFlow("")
    val nickName: StateFlow<String> = _nickName

    fun changeNickName(newNickName: String) {
        _nickName.value = newNickName
    }
}