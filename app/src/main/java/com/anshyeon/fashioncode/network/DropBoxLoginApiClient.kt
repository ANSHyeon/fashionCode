package com.anshyeon.fashioncode.network

import com.anshyeon.fashioncode.data.model.DropBoxAuthResponse
import com.anshyeon.fashioncode.network.model.ApiResponse
import retrofit2.http.POST
import retrofit2.http.Query

interface DropBoxLoginApiClient {

    @POST("oauth2/token")
    suspend fun getDropBoxToken(
        @Query("grant_type") grantType: String,
        @Query("refresh_token") refreshToken: String,
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String
    ): ApiResponse<DropBoxAuthResponse>
}