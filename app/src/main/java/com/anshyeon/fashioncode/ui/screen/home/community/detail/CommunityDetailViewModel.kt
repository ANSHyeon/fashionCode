package com.anshyeon.fashioncode.ui.screen.home.community.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.data.model.Comment
import com.anshyeon.fashioncode.data.model.Post
import com.anshyeon.fashioncode.data.model.Reply
import com.anshyeon.fashioncode.data.model.User
import com.anshyeon.fashioncode.data.repository.AuthRepository
import com.anshyeon.fashioncode.data.repository.CommentRepository
import com.anshyeon.fashioncode.data.repository.PostRepository
import com.anshyeon.fashioncode.data.repository.ReplyRepository
import com.anshyeon.fashioncode.network.extentions.onError
import com.anshyeon.fashioncode.network.extentions.onException
import com.anshyeon.fashioncode.network.extentions.onSuccess
import com.anshyeon.fashioncode.ui.graph.DetailHomeScreen
import com.anshyeon.fashioncode.util.SerializationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class CommunityDetailViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val authRepository: AuthRepository,
    private val commentRepository: CommentRepository,
    private val replyRepository: ReplyRepository,
) : ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    var post: StateFlow<Post?> = _post

    private val _user = MutableStateFlow<User?>(null)
    var user: StateFlow<User?> = _user

    private val _commentBody = MutableStateFlow("")
    var commentBody: StateFlow<String> = _commentBody

    private val _commentList = MutableStateFlow<List<Comment>>(emptyList())
    var commentList: StateFlow<List<Comment>> = _commentList

    private val _addedCommentList = MutableStateFlow<List<Comment>>(emptyList())
    var addedCommentList: StateFlow<List<Comment>> = _addedCommentList

    private val _isGetPostLoading = MutableStateFlow(false)
    val isGetPostLoading: StateFlow<Boolean> = _isGetPostLoading

    private val _isGetUserLoading = MutableStateFlow(false)
    val isGetUserLoading: StateFlow<Boolean> = _isGetUserLoading

    private val _isCreateCommentLoading = MutableStateFlow(false)
    val isCreateCommentLoading: StateFlow<Boolean> = _isCreateCommentLoading

    private val _isGetCommentListLoading = MutableStateFlow(false)
    val isGetCommentListLoading: StateFlow<Boolean> = _isGetCommentListLoading

    private val _isGetUserComplete = MutableStateFlow(false)
    val isGetUserComplete: StateFlow<Boolean> = _isGetUserComplete

    private val _isGetPostComplete = MutableStateFlow(false)
    val isGetPostComplete: StateFlow<Boolean> = _isGetPostComplete

    private val _isGetCommentListComplete = MutableStateFlow(false)
    val isGetCommentListComplete: StateFlow<Boolean> = _isGetCommentListComplete

    private val _snackBarText = MutableStateFlow("")
    val snackBarText: StateFlow<String> = _snackBarText

    private val _showSnackBar = MutableStateFlow(false)
    val showSnackBar: StateFlow<Boolean> = _showSnackBar

    fun getCommentList(postId: String) {
        val tempReplyList = MutableStateFlow<List<Reply>>(emptyList())

        _isGetCommentListLoading.value = true
        viewModelScope.launch {
            transformCommentList(postId).map {
                it.map { comment ->
                    tempReplyList.value = emptyList()
                    val response = replyRepository.getReplyList(
                        comment.commentId,
                        {},
                        {}
                    )
                    response.collectLatest { replys ->
                        tempReplyList.value = replys
                    }
                    comment.copy(
                        replyList = tempReplyList.value.sortedBy { reply -> reply.createdDate }
                    )
                }
            }.onCompletion {
                _isGetCommentListLoading.value = false
                _isGetCommentListComplete.value = true
            }.collectLatest {
                _commentList.value = it
            }
        }
    }

    private fun transformCommentList(postId: String): Flow<List<Comment>> {
        return commentRepository.getCommentList(
            postId,
            onComplete = {},
            onError = {
                _showSnackBar.value = true
                _snackBarText.value = "잠시 후 다시 시도해 주십시오"
            }
        ).map {
            it.sortedBy { comment -> comment.createdDate }
        }
    }

    fun createComment(postId: String) {
        _isCreateCommentLoading.value = true
        viewModelScope.launch {
            val result = commentRepository.createComment(
                _commentBody.value,
                postId,
            )
            result.onSuccess { savedComment ->
                _isCreateCommentLoading.value = false
                _commentBody.value = ""
                _addedCommentList.value =
                    _addedCommentList.value.toMutableList().apply { add(savedComment) }
            }.onError { _, _ ->
                _isCreateCommentLoading.value = false
                _showSnackBar.value = true
                _snackBarText.value = "잠시 후 다시 시도해 주십시오"
            }.onException {
                _isCreateCommentLoading.value = false
                _showSnackBar.value = true
                _snackBarText.value = "잠시 후 다시 시도해 주십시오"
            }
        }
    }

    fun getPost(postId: String) {
        _isGetPostLoading.value = true
        viewModelScope.launch {
            val response = postRepository.getPost(
                postId,
                onComplete = {
                    _isGetPostLoading.value = false
                    _isGetPostComplete.value = true
                },
                onError = {
                    _showSnackBar.value = true
                    _snackBarText.value = "잠시 후 다시 시도해 주십시오"
                }
            )
            response.collectLatest {
                it.onSuccess { post ->
                    _post.value = post
                    getUser(post.writer)
                    if (post.commentList.isNotEmpty()) {
                        getCommentList(post.postId)
                    }
                }
            }
        }
    }

    private fun getUser(userId: String) {
        _isGetUserLoading.value = true
        viewModelScope.launch {
            val response = authRepository.getUserInfo(
                userId,
                onComplete = {
                    _isGetUserLoading.value = false
                    _isGetUserComplete.value = true
                },
                onError = {
                    _showSnackBar.value = true
                    _snackBarText.value = "잠시 후 다시 시도해 주십시오"
                }
            )
            response.collectLatest {
                it.onSuccess { user ->
                    _user.value = user
                }
            }
        }
    }

    fun changeCommentBody(newBody: String) {
        _commentBody.value = newBody
    }

    fun dismissSnackBar() {
        _showSnackBar.value = false
    }

    fun navigateCommunityReply(navController: NavHostController, comment: Comment) {
        val commentJson = SerializationUtils.toJson(comment)
        val encodedUrl = URLEncoder.encode(commentJson, StandardCharsets.UTF_8.toString())
        navController.navigate("${DetailHomeScreen.CommunityReply.route}/${encodedUrl}")
    }

    fun navigateOtherUserProfile(navController: NavHostController, userId: String?) {
        navController.navigate("${DetailHomeScreen.OtherProfile.route}/${userId}")
    }

    fun navigateBack(navController: NavHostController) {
        navController.popBackStack()
    }
}