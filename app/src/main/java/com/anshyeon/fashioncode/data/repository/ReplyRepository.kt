package com.anshyeon.fashioncode.data.repository

import com.anshyeon.fashioncode.data.PreferenceManager
import com.anshyeon.fashioncode.data.dataSource.ImageDataSource
import com.anshyeon.fashioncode.data.dataSource.UserDataSource
import com.anshyeon.fashioncode.data.model.Reply
import com.anshyeon.fashioncode.network.FireBaseApiClient
import com.anshyeon.fashioncode.network.extentions.onError
import com.anshyeon.fashioncode.network.extentions.onException
import com.anshyeon.fashioncode.network.extentions.onSuccess
import com.anshyeon.fashioncode.network.model.ApiResponse
import com.anshyeon.fashioncode.network.model.ApiResultException
import com.anshyeon.fashioncode.network.model.ApiResultSuccess
import com.anshyeon.fashioncode.util.Constants
import com.anshyeon.fashioncode.util.DateFormatText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion

import javax.inject.Inject

class ReplyRepository @Inject constructor(
    private val fireBaseApiClient: FireBaseApiClient,
    private val userDataSource: UserDataSource,
    private val imageDataSource: ImageDataSource,
    private val preferenceManager: PreferenceManager,
) {

    suspend fun createReply(
        body: String,
        commentId: String,
        userId: String,
    ): ApiResponse<Reply> {
        val currentTime = System.currentTimeMillis()
        val replyId = userId + currentTime
        val reply = Reply(
            replyId,
            commentId,
            body,
            userId,
            preferenceManager.getString(Constants.KEY_USER_NICKNAME, ""),
            DateFormatText.getCurrentTime(),
            preferenceManager.getString(Constants.KEY_USER_PROFILE_URI, ""),
        )
        return try {
            fireBaseApiClient.createReply(
                userDataSource.getIdToken(),
                reply
            )
            ApiResultSuccess(
                reply.copy(
                    profileImageUrl = reply.profileImageUri
                        ?.let { imageDataSource.downloadImage(it) }
                )
            )
        } catch (e: Exception) {
            ApiResultException(e)
        }
    }

    fun getReplyList(
        commentId: String,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<List<Reply>> = flow {
        try {
            val response = fireBaseApiClient.getReplyList(
                userDataSource.getIdToken(),
                "\"${commentId}\""
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