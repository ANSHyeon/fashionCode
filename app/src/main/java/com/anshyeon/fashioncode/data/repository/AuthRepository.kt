package com.anshyeon.fashioncode.data.repository

import android.net.Uri
import com.anshyeon.fashioncode.data.PreferenceManager
import com.anshyeon.fashioncode.data.dataSource.ImageDataSource
import com.anshyeon.fashioncode.data.dataSource.UserDataSource
import com.anshyeon.fashioncode.data.model.User
import com.anshyeon.fashioncode.network.FireBaseApiClient
import com.anshyeon.fashioncode.network.extentions.onError
import com.anshyeon.fashioncode.network.extentions.onException
import com.anshyeon.fashioncode.network.extentions.onSuccess
import com.anshyeon.fashioncode.network.model.ApiResponse
import com.anshyeon.fashioncode.network.model.ApiResultException
import com.anshyeon.fashioncode.network.model.ApiResultSuccess
import com.anshyeon.fashioncode.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val fireBaseApiClient: FireBaseApiClient,
    private val preferenceManager: PreferenceManager,
    private val userDataSource: UserDataSource,
    private val imageDataSource: ImageDataSource,
) {

    fun getLocalIdToken(): String {
        return preferenceManager.getString(Constants.KEY_GOOGLE_ID_TOKEN, "")
    }

    suspend fun saveUserInfo(nickname: String, uri: String?) {
        with(preferenceManager) {
            setGoogleIdToken(
                Constants.KEY_GOOGLE_ID_TOKEN,
                userDataSource.getIdToken()
            )
            setUserNickName(
                Constants.KEY_USER_NICKNAME,
                nickname
            )
            uri?.let {
                setUserImage(
                    Constants.KEY_USER_PROFILE_URI,
                    it
                )
            }
        }
    }

    fun getUserId(): String {
        return userDataSource.getEmail().replace('@', 'a').replace('.', 'a')
    }

    suspend fun getUser(): ApiResponse<Map<String, User>> {
        return try {
            fireBaseApiClient.getUser(
                userDataSource.getIdToken(),
                "\"${getUserId()}\""
            )
        } catch (e: Exception) {
            ApiResultException(e)
        }
    }

    fun getUserInfo(
        userId: String,
        onComplete: () -> Unit,
        onError: () -> Unit
    ): Flow<ApiResponse<User>> = flow {
        try {
            val response = fireBaseApiClient.getUser(
                userDataSource.getIdToken(),
                "\"${userId}\""
            )
            response.onSuccess { data ->
                if (data.values.isNotEmpty()) {
                    emit(
                        ApiResultSuccess(data.values.first())
                    )
                }
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

    suspend fun createUser(
        nickname: String,
        uri: Uri?
    ): ApiResponse<Unit> {
        return try {
            val uriLocation = uri?.let { imageDataSource.uploadImage(it) }
            val userId = userDataSource.getEmail().replace('@', 'a').replace('.', 'a')
            val user = User(
                userId = userId,
                nickName = nickname,
                email = userDataSource.getEmail(),
                profileUri = uriLocation,
            )
            preferenceManager.setUserImage(Constants.KEY_USER_PROFILE_URI, uriLocation)
            fireBaseApiClient.createUser(
                userDataSource.getIdToken(),
                user
            )
        } catch (e: Exception) {
            ApiResultException(e)
        }
    }
}