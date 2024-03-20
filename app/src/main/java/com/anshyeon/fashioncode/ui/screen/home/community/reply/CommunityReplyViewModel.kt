package com.anshyeon.fashioncode.ui.screen.home.community.reply

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.data.model.Comment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CommunityReplyViewModel @Inject constructor(
) : ViewModel() {

    private val _replyBody = MutableStateFlow("")
    var replyBody: StateFlow<String> = _replyBody

    private val _commentList = MutableStateFlow<List<Comment>>(emptyList())
    var commentList: StateFlow<List<Comment>> = _commentList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isGetComplete = MutableStateFlow(false)
    val isGetComplete: StateFlow<Boolean> = _isGetComplete

    private val _snackBarText = MutableStateFlow("")
    val snackBarText: StateFlow<String> = _snackBarText

    private val _showSnackBar = MutableStateFlow(false)
    val showSnackBar: StateFlow<Boolean> = _showSnackBar

    fun changeCommentBody(newBody: String) {
        _replyBody.value = newBody
    }

    fun dismissSnackBar() {
        _showSnackBar.value = false
    }

    fun navigateBack(navController: NavHostController) {
        navController.popBackStack()
    }
}
