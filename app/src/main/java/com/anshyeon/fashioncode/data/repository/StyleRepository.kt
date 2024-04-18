package com.anshyeon.fashioncode.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.anshyeon.fashioncode.BuildConfig
import com.anshyeon.fashioncode.data.dataSource.ImageDataSource
import com.anshyeon.fashioncode.data.dataSource.UserDataSource
import com.anshyeon.fashioncode.data.local.dao.ClothesDao
import com.anshyeon.fashioncode.data.local.dao.StyleDao
import com.anshyeon.fashioncode.data.model.AdobeInput
import com.anshyeon.fashioncode.data.model.AdobeOutPut
import com.anshyeon.fashioncode.data.model.AdobeRequestBody
import com.anshyeon.fashioncode.data.model.Clothes
import com.anshyeon.fashioncode.data.model.ClothesType
import com.anshyeon.fashioncode.data.model.CommitInfo
import com.anshyeon.fashioncode.data.model.DropBoxDownloadRequestBody
import com.anshyeon.fashioncode.data.model.DropBoxRequestBody
import com.anshyeon.fashioncode.data.model.LocalStyle
import com.anshyeon.fashioncode.data.model.Style
import com.anshyeon.fashioncode.network.AdobeApiClient
import com.anshyeon.fashioncode.network.AdobeLoginApiClient
import com.anshyeon.fashioncode.network.DropboxApiClient
import com.anshyeon.fashioncode.network.FireBaseApiClient
import com.anshyeon.fashioncode.network.extentions.onError
import com.anshyeon.fashioncode.network.extentions.onException
import com.anshyeon.fashioncode.network.extentions.onSuccess
import com.anshyeon.fashioncode.network.model.ApiResponse
import com.anshyeon.fashioncode.network.model.ApiResultException
import com.anshyeon.fashioncode.util.DateFormatText
import kotlinx.coroutines.delay
import com.anshyeon.fashioncode.util.ImageTypeConvertor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion

import javax.inject.Inject

class StyleRepository @Inject constructor(
    private val adobeApiClient: AdobeApiClient,
    private val adobeLoginApiClient: AdobeLoginApiClient,
    private val dropBoxApiClient: DropboxApiClient,
    private val fireBaseApiClient: FireBaseApiClient,
    private val imageDataSource: ImageDataSource,
    private val userDataSource: UserDataSource,
    private val clothesDao: ClothesDao,
    private val styleDao: StyleDao,
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

    suspend fun createClothes(
        currentClothesType: ClothesType,
        adobeToken: String?,
        dropBoxToken: String?,
        dropBoxLink: String?,
        path: String?,
        context: Context,
        onError: () -> Unit
    ) {
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
                result?.let { imageUri ->
                    val userId = userDataSource.getUserId()
                    val clothes = Clothes(
                        userId + path,
                        userId,
                        currentClothesType,
                        ImageTypeConvertor.uriToBitmap(context, imageUri),
                    )
                    insertClothes(clothes)
                } ?: throw Exception()
            }.onException {
                onError()
            }.onError { code, message ->
                onError()
            }
        } catch (e: Exception) {
            onError()
        }
    }

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

    suspend fun createStylePost(
        bitmap: Bitmap
    ): ApiResponse<Unit> {
        val userId = userDataSource.getUserId()
        val currentTime = System.currentTimeMillis()
        val styleId = userId + currentTime
        val imageUrl = imageDataSource.downloadImage(imageDataSource.uploadBitMap(bitmap))
        val style = Style(
            styleId,
            userId,
            DateFormatText.getCurrentTime(),
            imageUrl
        )
        return try {
            fireBaseApiClient.createStyle(
                userDataSource.getIdToken(),
                style
            )
        } catch (e: Exception) {
            ApiResultException(e)
        }
    }

    fun getStyleList(
        onComplete: () -> Unit,
        onError: () -> Unit
    ): Flow<List<Style>> = flow {
        try {
            val response = fireBaseApiClient.getStyleList(
                userDataSource.getIdToken()
            )
            response.onSuccess { data ->
                emit(data.map { entry ->
                    entry.value
                })
            }.onError { _, _ ->
                onError()
            }.onException {
                onError()
            }
        } catch (e: Exception) {
            onError()
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)

    fun getStyleListWithWriter(
        userId: String,
        onComplete: () -> Unit,
        onError: () -> Unit
    ): Flow<List<Style>> = flow {
        try {
            val response = fireBaseApiClient.getStyleListWithWriter(
                userDataSource.getIdToken(),
                "\"${userId}\""
            )
            response.onSuccess { data ->
                emit(data.map { entry ->
                    entry.value
                })
            }.onError { _, _ ->
                onError()
            }.onException {
                onError()
            }
        } catch (e: Exception) {
            onError()
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)

    fun getStyleLikeList(
        styleId: String,
        onComplete: () -> Unit,
        onError: () -> Unit
    ): Flow<List<String>> = flow {
        try {
            val response = fireBaseApiClient.getStyleLikes(
                styleId,
                userDataSource.getIdToken()
            )
            response.onSuccess { data ->
                emit(data.map {
                    it.value
                })
            }.onError { _, _ ->
                onError()
            }.onException {
                onError()
            }
        } catch (e: Exception) {
            onError()
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)

    suspend fun createLike(
        styleId: String
    ): ApiResponse<Unit> {
        return try {
            fireBaseApiClient.createStyleLike(
                styleId,
                userDataSource.getUserId(),
                userDataSource.getIdToken(),
                userDataSource.getUserId()
            )
        } catch (e: Exception) {
            ApiResultException(e)
        }
    }

    suspend fun deleteLike(
        styleId: String
    ): ApiResponse<Unit> {
        return try {
            fireBaseApiClient.deleteStyleLike(
                styleId,
                userDataSource.getUserId(),
                userDataSource.getIdToken(),
            )
        } catch (e: Exception) {
            ApiResultException(e)
        }
    }

    fun getClothesListByRoom(
        onComplete: () -> Unit
    ): Flow<List<Clothes>> {
        return clothesDao.getAllClothesList()
            .onCompletion { onComplete() }
    }

    private suspend fun insertClothes(clothes: Clothes) {
        clothesDao.insert(clothes)
    }

    suspend fun deleteClothes(
        clothes: Clothes,
        onError: () -> Unit
    ) {
        try {
            clothesDao.deleteClothe(clothes)
        } catch (e: Exception) {
            onError()
        }
    }

    suspend fun saveStyle(
        bitmap: Bitmap,
        selectedDate: String,
        onError: () -> Unit
    ){
        try {
            val userId = userDataSource.getUserId()
            val style = LocalStyle(
                userId + bitmap.toString(),
                userId,
                selectedDate,
                bitmap,
            )
            insertStyle(style)
        } catch (e: Exception) {
            onError()
        }
    }

    fun getStyleListByRoom(
        onComplete: () -> Unit
    ): Flow<List<LocalStyle>> {
        return styleDao.getAllStyleList().onCompletion { onComplete() }
    }

    private suspend fun insertStyle(style: LocalStyle) {
        styleDao.insert(style)
    }
}