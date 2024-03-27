package com.anshyeon.fashioncode.data.repository

import android.graphics.Bitmap
import com.anshyeon.fashioncode.BuildConfig
import com.anshyeon.fashioncode.data.dataSource.ImageDataSource
import com.anshyeon.fashioncode.data.dataSource.UserDataSource
import com.anshyeon.fashioncode.data.local.dao.ClothesDao
import com.anshyeon.fashioncode.data.model.AdobeInput
import com.anshyeon.fashioncode.data.model.AdobeOutPut
import com.anshyeon.fashioncode.data.model.AdobeRequestBody
import com.anshyeon.fashioncode.data.model.Clothes
import com.anshyeon.fashioncode.data.model.ClothesType
import com.anshyeon.fashioncode.data.model.CommitInfo
import com.anshyeon.fashioncode.data.model.DropBoxDownloadRequestBody
import com.anshyeon.fashioncode.data.model.DropBoxRequestBody
import com.anshyeon.fashioncode.network.AdobeApiClient
import com.anshyeon.fashioncode.network.AdobeLoginApiClient
import com.anshyeon.fashioncode.network.DropboxApiClient
import com.anshyeon.fashioncode.network.extentions.onError
import com.anshyeon.fashioncode.network.extentions.onException
import com.anshyeon.fashioncode.network.extentions.onSuccess
import com.anshyeon.fashioncode.network.model.ApiResponse
import com.anshyeon.fashioncode.network.model.ApiResultSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion

import javax.inject.Inject

class StyleRepository @Inject constructor(
    private val adobeApiClient: AdobeApiClient,
    private val adobeLoginApiClient: AdobeLoginApiClient,
    private val dropBoxApiClient: DropboxApiClient,
    private val imageDataSource: ImageDataSource,
    private val userDataSource: UserDataSource,
    private val clothesDao: ClothesDao,
) {


    suspend fun getDropBoxLink(token: String?, bitmap: Bitmap): List<String?> {
        val imageLocation = imageDataSource.uploadBitMap(bitmap)
        val path = imageLocation.substringAfterLast("images/clothes_")
        val dropBoxRequestBody = DropBoxRequestBody(
            CommitInfo(
                true,
                "add",
                false,
                "/adobe/${path}.png",
                false
            ),
            3600
        )
        var result: String? = null
        return try {
            val response = dropBoxApiClient.getDropBoxLink(
                "Bearer ${token}",
                "application/json",
                dropBoxRequestBody
            )
            response.onSuccess {
                result = it.values.first()
            }
            listOf(result, path)
        } catch (e: Exception) {
            listOf(result, path)
        }
    }

    fun createClothes(
        currentClothesType: ClothesType,
        adobeToken: String?,
        dropBoxToken: String?,
        dropBoxLink: String?,
        path: String?,
        onComplete: () -> Unit,
        onError: () -> Unit
    ): Flow<ApiResponse<Clothes>> = flow {
        try {
            val inputHref = "${BuildConfig.FIREBASE_STORAGE_URL}${path}?alt=media"
            val adobeRequestBody = AdobeRequestBody(
                AdobeInput(inputHref, "external"),
                AdobeOutPut(dropBoxLink ?: "", "dropbox")
            )
            val response = adobeApiClient.cutoutImage(
                "Bearer ${adobeToken}",
                BuildConfig.ADOBE_CLIENT_ID,
                "application/json",
                adobeRequestBody
            )
            response.onSuccess {
                delay(8000)
                val result = getDropBoxImage(dropBoxToken, path)
                result?.let {
                    val userId = userDataSource.getUserId()
                    val clothes = Clothes(
                        userId + path,
                        userId,
                        currentClothesType,
                        imageUrl = it
                    )
                    insertClothes(clothes)
                    emit(ApiResultSuccess(clothes))
                } ?: throw Exception()
            }.onException {
                onError()
            }.onError { code, message ->
                onError()
            }
        } catch (e: Exception) {
            onError()
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)

    suspend fun getAdobeLoginToken(): String? {
        var result: String? = null
        return try {
            val response = adobeLoginApiClient.getAdobeToken(
                "application/x-www-form-urlencoded",
                "client_credentials",
                BuildConfig.ADOBE_CLIENT_ID,
                BuildConfig.ADOBE_CLIENT_SECRET,
                "openid,AdobeID,read_organizations"
            )
            response.onSuccess {
                result = it.accessToken
            }
            result
        } catch (e: Exception) {
            result
        }
    }

    suspend fun getDropBoxToken(): String? {
        var result: String? = null
        return try {
            val response = dropBoxApiClient.getDropBoxToken(
                "refresh_token",
                BuildConfig.DROPBOX_REFRESH_TOKEN,
                BuildConfig.DROPBOX_APP_KEY,
                BuildConfig.DROPBOX_APP_SECRET
            )
            response.onSuccess {
                result = it.accessToken
            }
            result
        } catch (e: Exception) {
            result
        }
    }

    suspend fun getDropBoxImage(token: String?, path: String?): String? {
        var result: String? = null
        return try {
            val requestBody = DropBoxDownloadRequestBody(
                "/adobe/${path}.png"
            )
            val response = dropBoxApiClient.getDropBoxDownloadLink(
                "Bearer ${token}",
                "application/json",
                requestBody
            )
            response.onSuccess {
                result = it.link
            }
            result
        } catch (e: Exception) {
            result
        }
    }

    private suspend fun insertClothes(clothes: Clothes) {
        clothesDao.insert(clothes)
    }
}