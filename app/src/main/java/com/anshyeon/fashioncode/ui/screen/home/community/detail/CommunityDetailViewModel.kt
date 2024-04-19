package com.anshyeon.fashioncode.ui.screen.home.community.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.data.model.Comment
import com.anshyeon.fashioncode.data.model.Post
import com.anshyeon.fashioncode.data.model.Reply
import com.anshyeon.fashioncode.data.repository.CommentRepository
import com.anshyeon.fashioncode.data.repository.PostRepository
import com.anshyeon.fashioncode.data.repository.ReplyRepository
import com.anshyeon.fashioncode.network.extentions.onError
import com.anshyeon.fashioncode.network.extentions.onException
import com.anshyeon.fashioncode.network.extentions.onSuccess
import com.anshyeon.fashioncode.ui.graph.DetailHomeScreen
import com.anshyeon.fashioncode.util.SerializationUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@HiltViewModel(assistedFactory = CommunityDetailViewModel.Factory::class)
class CommunityDetailViewModel @AssistedInject constructor(
    @Assisted private val postId: String,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val replyRepository: ReplyRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(postId: String): CommunityDetailViewModel
    }

    private val _post = MutableStateFlow<Post?>(null)
    var post: StateFlow<Post?> = _post

    private val _commentBody = MutableStateFlow("")
    var commentBody: StateFlow<String> = _commentBody

    private val _addedCommentList = MutableStateFlow<List<Comment>>(emptyList())
    var addedCommentList: StateFlow<List<Comment>> = _addedCommentList

    private val _isGetPostLoading = MutableStateFlow(false)
    val isGetPostLoading: StateFlow<Boolean> = _isGetPostLoading

    private val _isCreateCommentLoading = MutableStateFlow(false)
    val isCreateCommentLoading: StateFlow<Boolean> = _isCreateCommentLoading

    private val _isGetCommentListLoading = MutableStateFlow(false)
    val isGetCommentListLoading: StateFlow<Boolean> = _isGetCommentListLoading

    private val _isGetPostComplete = MutableStateFlow(false)
    val isGetPostComplete: StateFlow<Boolean> = _isGetPostComplete

    private val _isGetCommentListComplete = MutableStateFlow(false)
    val isGetCommentListComplete: StateFlow<Boolean> = _isGetCommentListComplete

    private val _snackBarText = MutableStateFlow("")
    val snackBarText: StateFlow<String> = _snackBarText

    private val _showSnackBar = MutableStateFlow(false)
    val showSnackBar: StateFlow<Boolean> = _showSnackBar

    private fun getCommentList(postId: String) {
        _isGetCommentListLoading.value = true
        viewModelScope.launch {
            transformCommentList(postId).map {
                val commentListWithReply = viewModelScope.async {
                    it.map { comment ->
                        viewModelScope.async {
                            val tempReplyList = mutableListOf<Reply>()
                            val response = replyRepository.getReplyList(
                                viewModelScope,
                                comment.commentId,
                                {},
                                {}
                            )
                            response.collectLatest { replys ->
                                tempReplyList.addAll(replys)
                            }
                            comment.copy(
                                replyList = tempReplyList.sortedBy { reply -> reply.createdDate }
                                    .toList()
                            )
                        }
                    }
                }
                commentListWithReply.await().map { it.await() }
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
            viewModelScope,
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

    private fun getPost(postId: String) {
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
                    if (post.commentList.isNotEmpty()) {
                        getCommentList(post.postId)
                    }
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

    fun setPostId(newPostId: String) {
        val isFirst = postId == null
        postId = newPostId
        if (isFirst) {
            getPost(postId!!)
            getCommentList(postId!!)
        }
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