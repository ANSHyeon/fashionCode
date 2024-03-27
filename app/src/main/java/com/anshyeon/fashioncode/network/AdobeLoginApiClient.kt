package com.anshyeon.fashioncode.network

import com.anshyeon.fashioncode.data.model.AdobeAuthResponse
import com.anshyeon.fashioncode.network.model.ApiResponse
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AdobeLoginApiClient {

    @POST("token/v3")
    suspend fun getAdobeToken(
        @Header("Content-type") contentType: String,
        @Query("grant_type") grantType: String,
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("scope") scope: String
    ): ApiResponse<AdobeAuthResponse>
}