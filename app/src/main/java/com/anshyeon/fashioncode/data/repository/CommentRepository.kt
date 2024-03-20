package com.anshyeon.fashioncode.data.repository

import com.anshyeon.fashioncode.data.PreferenceManager
import com.anshyeon.fashioncode.data.dataSource.ImageDataSource
import com.anshyeon.fashioncode.data.dataSource.UserDataSource
import com.anshyeon.fashioncode.data.model.Comment
import com.anshyeon.fashioncode.network.FireBaseApiClient
import com.anshyeon.fashioncode.network.extentions.onError
import com.anshyeon.fashioncode.network.extentions.onException
import com.anshyeon.fashioncode.network.extentions.onSuccess
import com.anshyeon.fashioncode.network.model.ApiResponse
import com.anshyeon.fashioncode.network.model.ApiResultException
import com.anshyeon.fashioncode.util.Constants
import com.anshyeon.fashioncode.util.DateFormatText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion

import javax.inject.Inject

class CommentRepository @Inject constructor(
    private val fireBaseApiClient: FireBaseApiClient,
    private val userDataSource: UserDataSource,
    private val imageDataSource: ImageDataSource,
    private val preferenceManager: PreferenceManager,
) {

    suspend fun createComment(
        body: String,
        postId: String,
        userId: String,
    ): ApiResponse<Unit> {
        val currentTime = System.currentTimeMillis()
        val commentId = userId + currentTime
        val comment = Comment(
            commentId,
            body,
            userId,
            preferenceManager.getString(Constants.KEY_USER_NICKNAME, ""),
            DateFormatText.getCurrentTime(),
            preferenceManager.getString(Constants.KEY_USER_PROFILE_URI, ""),
        )
        return try {
            fireBaseApiClient.createComment(
                postId,
                userDataSource.getIdToken(),
                comment
            )
        } catch (e: Exception) {
            ApiResultException(e)
        }
    }

    fun getCommentList(
        postId: String,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<List<Comment>> = flow {
        try {
            val response = fireBaseApiClient.getCommentList(
                postId,
                userDataSource.getIdToken()
            )
            response.onSuccess { data ->
                emit(
                    data.map { entry ->
                        entry.value.run {
                            copy(
                                profileImageUrl = profileImageUri
                                    ?.let { imageDataSource.downloadImage(it) }
                            )
                        }
                    }
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