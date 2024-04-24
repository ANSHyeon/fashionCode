package com.anshyeon.fashioncode.data.repository

import com.anshyeon.fashioncode.BuildConfig
import com.anshyeon.fashioncode.data.datastore.DataStore
import com.anshyeon.fashioncode.network.AdobeLoginApiClient
import com.anshyeon.fashioncode.network.DropBoxLoginApiClient
import com.anshyeon.fashioncode.network.extentions.onSuccess
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TokenRepository @Inject constructor(
    private val adobeLoginApiClient: AdobeLoginApiClient,
    private val dropBoxLoginApiClient: DropBoxLoginApiClient,
    private val dataStore: DataStore,
) {

    suspend fun saveAdobeToken(adobeToken: String?) {
        dataStore.saveAdobeToken(adobeToken ?: "")
    }

    suspend fun saveDropBoxToken(dropBoxToken: String?) {
        dataStore.saveDropBoxToken(dropBoxToken ?: "")
    }

    suspend fun getAdobeToken(): Flow<String> {
        return dataStore.getAdobeToken()
    }

    suspend fun getDropBoxToken(): Flow<String> {
        return dataStore.getDropBoxToken()
    }

    suspend fun getAdobeRefreshToken(): String? {
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

    suspend fun getDropBoxRefreshToken(): String? {
        var result: String? = null
        return try {
            val response = dropBoxLoginApiClient.getDropBoxToken(
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
}