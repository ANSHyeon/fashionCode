package com.anshyeon.fashioncode.ui.screen.home.community.reply

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.data.model.Reply
import com.anshyeon.fashioncode.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CommunityReplyViewModel @Inject constructor(
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    var user: StateFlow<User?> = _user

    private val _replyBody = MutableStateFlow("")
    var replyBody: StateFlow<String> = _replyBody

    private val _replyList = MutableStateFlow<List<Reply>>(emptyList())
    var replyList: StateFlow<List<Reply>> = _replyList

    private val _isCreateReplyLoading = MutableStateFlow(false)
    val isCreateReplyLoading: StateFlow<Boolean> = _isCreateReplyLoading

    private val _isGetReplyLoading = MutableStateFlow(false)
    val isGetReplyLoading: StateFlow<Boolean> = _isGetReplyLoading

    private val _isGetUserLoading = MutableStateFlow(false)
    val isGetUserLoading: StateFlow<Boolean> = _isGetUserLoading

    private val _isGetReplyComplete = MutableStateFlow(false)
    val isGetReplyComplete: StateFlow<Boolean> = _isGetReplyComplete

    private val _isGetUserComplete = MutableStateFlow(false)
    val isGetUserComplete: StateFlow<Boolean> = _isGetUserComplete

    private val _snackBarText = MutableStateFlow("")
    val snackBarText: StateFlow<String> = _snackBarText

    private val _showSnackBar = MutableStateFlow(false)
    val showSnackBar: StateFlow<Boolean> = _showSnackBar

    fun changeReplyBody(newBody: String) {
        _replyBody.value = newBody
    }

    fun dismissSnackBar() {
        _showSnackBar.value = false
    }

    fun navigateBack(navController: NavHostController) {
        navController.popBackStack()
    }
}
