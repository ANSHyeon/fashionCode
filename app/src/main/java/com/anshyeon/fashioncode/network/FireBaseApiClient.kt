package com.anshyeon.fashioncode.network

import com.anshyeon.fashioncode.data.model.User
import com.anshyeon.fashioncode.network.model.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FireBaseApiClient {

    @GET("users.json?orderBy=\"userId\"")
    suspend fun getUser(
        @Query("auth") auth: String?,
        @Query("equalTo") userId: String
    ): ApiResponse<Map<String, User>>
}