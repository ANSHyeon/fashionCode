package com.anshyeon.fashioncode.network

import com.anshyeon.fashioncode.data.model.AdobeRequestBody
import com.anshyeon.fashioncode.data.model.AdobeCutoutResponse
import com.anshyeon.fashioncode.network.model.ApiResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AdobeApiClient {

    @POST("sensei/cutout")
    suspend fun cutoutImage(
        @Header("x-api-key") apiKey: String,
        @Header("content-type") contentType: String,
        @Body requestBody: AdobeRequestBody
    ): ApiResponse<AdobeCutoutResponse>
}