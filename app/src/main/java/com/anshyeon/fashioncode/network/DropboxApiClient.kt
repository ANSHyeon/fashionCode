package com.anshyeon.fashioncode.network

import com.anshyeon.fashioncode.data.model.DropBoxDownloadRequestBody
import com.anshyeon.fashioncode.data.model.DropBoxDownloadResponse
import com.anshyeon.fashioncode.data.model.DropBoxRequestBody
import com.anshyeon.fashioncode.network.model.ApiResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DropboxApiClient {

    @POST("2/files/get_temporary_upload_link")
    suspend fun getDropBoxLink(
        @Header("Content-Type") contentType: String,
        @Body requestBody: DropBoxRequestBody
    ): ApiResponse<Map<String, String>>

    @POST("2/files/get_temporary_link")
    suspend fun getDropBoxDownloadLink(
        @Header("Content-Type") contentType: String,
        @Body requestBody: DropBoxDownloadRequestBody
    ): ApiResponse<DropBoxDownloadResponse>
}