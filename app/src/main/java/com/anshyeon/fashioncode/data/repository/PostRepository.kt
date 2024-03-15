package com.anshyeon.fashioncode.data.repository

import android.net.Uri
import com.anshyeon.fashioncode.data.dataSource.ImageDataSource
import com.anshyeon.fashioncode.data.dataSource.UserDataSource
import com.anshyeon.fashioncode.data.model.Post
import com.anshyeon.fashioncode.data.model.User
import com.anshyeon.fashioncode.network.FireBaseApiClient
import com.anshyeon.fashioncode.network.model.ApiResponse
import com.anshyeon.fashioncode.network.model.ApiResultException
import com.anshyeon.fashioncode.util.DateFormatText

import javax.inject.Inject

class PostRepository @Inject constructor(
    private val fireBaseApiClient: FireBaseApiClient,
    private val userDataSource: UserDataSource,
    private val imageDataSource: ImageDataSource,
) {

    suspend fun createPost(
        title: String,
        body: String,
        currentUser: User,
        imageList: List<Uri>
    ): ApiResponse<Map<String, String>> {
        val currentTime = System.currentTimeMillis()
        val postId = currentUser.userId + currentTime
        val imageLocations = imageDataSource.uploadImages(imageList)
        val post = Post(
            postId,
            title,
            body,
            currentUser,
            DateFormatText.getCurrentTime(),
            imageLocations
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
}