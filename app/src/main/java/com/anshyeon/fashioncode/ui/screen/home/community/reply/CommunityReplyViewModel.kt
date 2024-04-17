package com.anshyeon.fashioncode.ui.screen.home.community.reply

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.data.model.Reply
import com.anshyeon.fashioncode.data.repository.ReplyRepository
import com.anshyeon.fashioncode.network.extentions.onError
import com.anshyeon.fashioncode.network.extentions.onException
import com.anshyeon.fashioncode.network.extentions.onSuccess
import com.anshyeon.fashioncode.ui.graph.DetailHomeScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityReplyViewModel @Inject constructor(
    private val replyRepository: ReplyRepository,
) : ViewModel() {

    private val _replyBody = MutableStateFlow("")
    var replyBody: StateFlow<String> = _replyBody

    private val _replyList = MutableStateFlow<List<Reply>>(emptyList())
    var replyList: StateFlow<List<Reply>> = _replyList

    private val _addedReplyList = MutableStateFlow<List<Reply>>(emptyList())
    var addedReplyList: StateFlow<List<Reply>> = _addedReplyList

    private val _isCreateReplyLoading = MutableStateFlow(false)
    val isCreateReplyLoading: StateFlow<Boolean> = _isCreateReplyLoading

    private val _isGetReplyLoading = MutableStateFlow(false)
    val isGetReplyLoading: StateFlow<Boolean> = _isGetReplyLoading

    private val _isGetReplyComplete = MutableStateFlow(false)
    val isGetReplyComplete: StateFlow<Boolean> = _isGetReplyComplete

    private val _snackBarText = MutableStateFlow("")
    val snackBarText: StateFlow<String> = _snackBarText

    private val _showSnackBar = MutableStateFlow(false)
    val showSnackBar: StateFlow<Boolean> = _showSnackBar

    fun getReplyList(commentId: String) {
        _isGetReplyLoading.value = true
        viewModelScope.launch {
            transformReplyList(commentId).onCompletion {
                _isGetReplyLoading.value = false
                _isGetReplyComplete.value = true
            }.collectLatest {
                _replyList.value = it
            }
        }
    }

    private fun transformReplyList(commentId: String): Flow<List<Reply>> {
        return replyRepository.getReplyList(
            viewModelScope,
            commentId,
            onComplete = { },
            onError = {
                _showSnackBar.value = true
                _snackBarText.value = "잠시 후 다시 시도해 주십시오"
            }
        ).map {
            it.sortedBy { reply -> reply.createdDate }
        }
    }

    fun createReply(commentId: String) {
        _isCreateReplyLoading.value = true
        viewModelScope.launch {
            val result = replyRepository.createReply(
                _replyBody.value,
                commentId,
            )
            result.onSuccess { savedReply ->
                _isCreateReplyLoading.value = false
                _replyBody.value = ""
                _addedReplyList.value =
                    _addedReplyList.value.toMutableList().apply { add(savedReply) }
            }.onError { _, _ ->
                _isCreateReplyLoading.value = false
                _showSnackBar.value = true
                _snackBarText.value = "잠시 후 다시 시도해 주십시오"
            }.onException {
                _isCreateReplyLoading.value = false
                _showSnackBar.value = true
                _snackBarText.value = "잠시 후 다시 시도해 주십시오"
            }
        }
    }

    fun changeReplyBody(newBody: String) {
        _replyBody.value = newBody
    }

    fun dismissSnackBar() {
        _showSnackBar.value = false
    }

    fun navigateOtherUserProfile(navController: NavHostController, userId: String?) {
        navController.navigate("${DetailHomeScreen.OtherProfile.route}/${userId}")
    }

    fun navigateBack(navController: NavHostController) {
        navController.popBackStack()
    }
}
