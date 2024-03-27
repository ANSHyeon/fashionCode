package com.anshyeon.fashioncode.network

import com.anshyeon.fashioncode.data.model.DropBoxAuthResponse
import com.anshyeon.fashioncode.data.model.DropBoxDownloadRequestBody
import com.anshyeon.fashioncode.data.model.DropBoxDownloadResponse
import com.anshyeon.fashioncode.data.model.DropBoxRequestBody
import com.anshyeon.fashioncode.network.model.ApiResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface DropboxApiClient {

    @POST("2/files/get_temporary_upload_link")
    suspend fun getDropBoxLink(
        @Header("Authorization") authorization: String,
        @Header("Content-Type") contentType: String,
        @Body requestBody: DropBoxRequestBody
    ): ApiResponse<Map<String, String>>

    @POST("2/files/get_temporary_link")
    suspend fun getDropBoxDownloadLink(
        @Header("Authorization") authorization: String,
        @Header("Content-Type") contentType: String,
        @Body requestBody: DropBoxDownloadRequestBody
    ): ApiResponse<DropBoxDownloadResponse>

    @POST("oauth2/token")
    suspend fun getDropBoxToken(
        @Query("grant_type") grantType: String,
        @Query("refresh_token") refreshToken: String,
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String
    ): ApiResponse<DropBoxAuthResponse>
}