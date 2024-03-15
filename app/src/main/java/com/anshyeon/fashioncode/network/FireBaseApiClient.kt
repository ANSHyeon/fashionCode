package com.anshyeon.fashioncode.network

import com.anshyeon.fashioncode.data.model.Post
import com.anshyeon.fashioncode.data.model.User
import com.anshyeon.fashioncode.network.model.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FireBaseApiClient {

    @GET("users.json?orderBy=\"userId\"")
    suspend fun getUser(
        @Query("auth") auth: String?,
        @Query("equalTo") userId: String
    ): ApiResponse<Map<String, User>>

    @PUT("users/{userId}.json")
    suspend fun createUser(
        @Path("userId") userId: String,
        @Query("auth") auth: String?,
        @Body user: User
    ): ApiResponse<Map<String, String>>

    @PUT("posts/{postId}.json")
    suspend fun createPost(
        @Path("postId") postId: String,
        @Query("auth") auth: String?,
        @Body post: Post
    ): ApiResponse<Map<String, String>>
}