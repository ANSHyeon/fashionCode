package com.anshyeon.fashioncode.data.repository

import android.net.Uri
import com.anshyeon.fashioncode.data.PreferenceManager
import com.anshyeon.fashioncode.data.dataSource.ImageDataSource
import com.anshyeon.fashioncode.data.dataSource.UserDataSource
import com.anshyeon.fashioncode.data.model.Post
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

class PostRepository @Inject constructor(
    private val fireBaseApiClient: FireBaseApiClient,
    private val userDataSource: UserDataSource,
    private val imageDataSource: ImageDataSource,
    private val preferenceManager: PreferenceManager,
) {

    suspend fun createPost(
        title: String,
        body: String,
        imageList: List<Uri>
    ): ApiResponse<Unit> {
        val userId = userDataSource.getUserId()
        val currentTime = System.currentTimeMillis()
        val postId = userId + currentTime
        val imageUrlList = imageDataSource.uploadImages(imageList)
        val post = Post(
            postId,
            title,
            body,
            userId,
            preferenceManager.getString(Constants.KEY_USER_NICKNAME, ""),
            preferenceManager.getString(Constants.KEY_USER_PROFILE_URL, ""),
            DateFormatText.getCurrentTime(),
            imageUrlList
        )
        return try {
            fireBaseApiClient.createPost(
                post.postId,
                userDataSource.getIdToken(),
                post
            )
        } catch (e: Exception) {
            ApiResultException(e)
        }
    }

    fun getPost(
        postId: String,
        onComplete: () -> Unit,
        onError: () -> Unit
    ): Flow<ApiResponse<Post>> = flow {
        try {
            val response = fireBaseApiClient.getPost(
                postId,
                userDataSource.getIdToken()
            )
            response.onSuccess { data ->
                emit(
                    ApiResultSuccess(data)
                )
            }.onError { _, _ ->
                onError()
            }.onException {
                onError()
            }
        } catch (e: Exception) {
            onError()
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)

    fun getPostList(
        onComplete: () -> Unit,
        onError: () -> Unit
    ): Flow<List<Post>> = flow {
        try {
            val response = fireBaseApiClient.getPostList(
                userDataSource.getIdToken()
            )
            response.onSuccess { data ->
                emit(data.map { entry ->
                    entry.value.run {
                        copy(
                            profileImageUrl = if (imageUrlList.isNotEmpty()) imageUrlList.first() else null
                        )
                    }
                })
            }.onError { _, _ ->
                onError()
            }.onException {
                onError()
            }
        } catch (e: Exception) {
            onError()
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)
}