package com.anshyeon.fashioncode.network

import com.anshyeon.fashioncode.data.model.Comment
import com.anshyeon.fashioncode.data.model.Post
import com.anshyeon.fashioncode.data.model.Reply
import com.anshyeon.fashioncode.data.model.User
import com.anshyeon.fashioncode.network.model.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FireBaseApiClient {

    @GET("users.json?orderBy=\"userId\"")
    suspend fun getUser(
        @Query("auth") auth: String?,
        @Query("equalTo") userId: String
    ): ApiResponse<Map<String, User>>

    @POST("users.json")
    suspend fun createUser(
        @Query("auth") auth: String?,
        @Body user: User
    ): ApiResponse<Unit>

    @GET("posts.json")
    suspend fun getPostList(
        @Query("auth") auth: String?,
    ): ApiResponse<Map<String, Post>>

    @GET("posts/{postId}.json")
    suspend fun getPost(
        @Path("postId") postId: String,
        @Query("auth") auth: String?,
    ): ApiResponse<Post>

    @PUT("posts/{postId}.json")
    suspend fun createPost(
        @Path("postId") postId: String,
        @Query("auth") auth: String?,
        @Body post: Post
    ): ApiResponse<Unit>

    @POST("comments.json")
    suspend fun createComment(
        @Query("auth") auth: String?,
        @Body comment: Comment
    ): ApiResponse<Unit>

    @GET("comments.json?orderBy=\"postId\"")
    suspend fun getCommentList(
        @Query("auth") auth: String?,
        @Query("equalTo") postId: String
    ): ApiResponse<Map<String, Comment>>

    @POST("replys.json")
    suspend fun createReply(
        @Query("auth") auth: String?,
        @Body reply: Reply
    ): ApiResponse<Unit>

    @GET("replys.json?orderBy=\"commentId\"")
    suspend fun getReplyList(
        @Query("auth") auth: String?,
        @Query("equalTo") commentId: String
    ): ApiResponse<Map<String, Reply>>
}