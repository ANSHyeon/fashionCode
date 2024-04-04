package com.anshyeon.fashioncode.data.repository

import com.anshyeon.fashioncode.data.PreferenceManager
import com.anshyeon.fashioncode.data.dataSource.UserDataSource
import com.anshyeon.fashioncode.data.model.Comment
import com.anshyeon.fashioncode.network.FireBaseApiClient
import com.anshyeon.fashioncode.network.extentions.onError
import com.anshyeon.fashioncode.network.extentions.onException
import com.anshyeon.fashioncode.network.extentions.onSuccess
import com.anshyeon.fashioncode.network.model.ApiResponse
import com.anshyeon.fashioncode.network.model.ApiResultException
import com.anshyeon.fashioncode.network.model.ApiResultSuccess
import com.anshyeon.fashioncode.util.Constants
import com.anshyeon.fashioncode.util.DateFormatText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion

import javax.inject.Inject

class CommentRepository @Inject constructor(
    private val fireBaseApiClient: FireBaseApiClient,
    private val userDataSource: UserDataSource,
    private val preferenceManager: PreferenceManager,
) {

    suspend fun createComment(
        body: String,
        postId: String,
    ): ApiResponse<Comment> {
        val userId = userDataSource.getUserId()
        val currentTime = System.currentTimeMillis()
        val commentId = userId + currentTime
        val comment = Comment(
            commentId,
            postId,
            body,
            userId,
            preferenceManager.getString(Constants.KEY_USER_NICKNAME, ""),
            DateFormatText.getCurrentTime(),
            preferenceManager.getString(Constants.KEY_USER_PROFILE_URL, ""),
        )
        return try {
            fireBaseApiClient.createComment(
                userDataSource.getIdToken(),
                comment
            )
            ApiResultSuccess(
                comment
            )
        } catch (e: Exception) {
            ApiResultException(e)
        }
    }

    fun getCommentList(
        viewModelScope: CoroutineScope,
        postId: String,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<List<Comment>> = flow {
        try {
            val response = fireBaseApiClient.getCommentList(
                userDataSource.getIdToken(),
                "\"${postId}\""
            )
            response.onSuccess { data ->
                val commentList = viewModelScope.async {
                    data.map { entry ->
                        entry.value
                    }
                }
                emit(
                    commentList.await()
                )
            }.onError { _, message ->
                onError(message)
            }.onException {
                onError(it.message)
            }
        } catch (e: Exception) {
            onError(e.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)
}