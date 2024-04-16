package com.anshyeon.fashioncode.network

import com.anshyeon.fashioncode.data.model.Comment
import com.anshyeon.fashioncode.data.model.Follow
import com.anshyeon.fashioncode.data.model.Post
import com.anshyeon.fashioncode.data.model.Reply
import com.anshyeon.fashioncode.data.model.Style
import com.anshyeon.fashioncode.data.model.User
import com.anshyeon.fashioncode.network.model.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
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

    @PATCH("users/{userKey}.json")
    suspend fun updateUser(
        @Path("userKey") userKey: String,
        @Query("auth") auth: String?,
        @Body updates: Map<String, String?>
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

    @POST("styles.json")
    suspend fun createStyle(
        @Query("auth") auth: String?,
        @Body style: Style
    ): ApiResponse<Unit>

    @GET("styles.json")
    suspend fun getStyleList(
        @Query("auth") auth: String?,
    ): ApiResponse<Map<String, Style>>

    @GET("styles.json?orderBy=\"writer\"")
    suspend fun getStyleListWithWriter(
        @Query("auth") auth: String?,
        @Query("equalTo") writer: String
    ): ApiResponse<Map<String, Style>>

    @PUT("likes/{styleId}/{userId}.json")
    suspend fun createStyleLike(
        @Path("styleId") styleId: String,
        @Path("userId") userId: String,
        @Query("auth") auth: String?,
        @Body body: String
    ): ApiResponse<Unit>

    @DELETE("likes/{styleId}/{userId}.json")
    suspend fun deleteStyleLike(
        @Path("styleId") styleId: String,
        @Path("userId") userId: String,
        @Query("auth") auth: String?,
    ): ApiResponse<Unit>

    @GET("likes/{styleId}.json")
    suspend fun getStyleLikes(
        @Path("styleId") styleId: String,
        @Query("auth") auth: String?,
    ): ApiResponse<Map<String, String>>

    @PUT("follows/{followId}.json")
    suspend fun createFollow(
        @Path("followId") styleId: String,
        @Query("auth") auth: String?,
        @Body body: Follow
    ): ApiResponse<Unit>

    @DELETE("follows/{followId}.json")
    suspend fun deleteFollow(
        @Path("followId") styleId: String,
        @Query("auth") auth: String?,
    ): ApiResponse<Unit>

    @GET("follows.json?orderBy=\"following\"")
    suspend fun getFollower(
        @Query("auth") auth: String?,
        @Query("equalTo") following: String
    ): ApiResponse<Map<String, Follow>>

    @GET("follows.json?orderBy=\"follower\"")
    suspend fun getFollowing(
        @Query("auth") auth: String?,
        @Query("equalTo") follower: String
    ): ApiResponse<Map<String, Follow>>
}