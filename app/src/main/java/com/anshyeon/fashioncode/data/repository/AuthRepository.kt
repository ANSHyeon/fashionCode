package com.anshyeon.fashioncode.data.repository

import com.anshyeon.fashioncode.data.PreferenceManager
import com.anshyeon.fashioncode.data.dataSource.UserDataSource
import com.anshyeon.fashioncode.data.model.User
import com.anshyeon.fashioncode.network.FireBaseApiClient
import com.anshyeon.fashioncode.network.model.ApiResponse
import com.anshyeon.fashioncode.network.model.ApiResultException
import com.anshyeon.fashioncode.util.Constants
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val fireBaseApiClient: FireBaseApiClient,
    private val preferenceManager: PreferenceManager,
    private val userDataSource: UserDataSource,
) {

    fun getLocalIdToken(): String? {
        return preferenceManager.getString(Constants.KEY_GOOGLE_ID_TOKEN, "")
    }

    suspend fun saveIdToken() {
        preferenceManager.setGoogleIdToken(
            Constants.KEY_GOOGLE_ID_TOKEN,
            userDataSource.getIdToken()
        )
    }

    suspend fun getUser(): ApiResponse<Map<String, User>> {
        return try {
            val userId = userDataSource.getEmail().replace('@', 'a').replace('.', 'a')
            fireBaseApiClient.getUser(
                userDataSource.getIdToken(),
                "\"${userId}\""
            )
        } catch (e: Exception) {
            ApiResultException(e)
        }
    }
}