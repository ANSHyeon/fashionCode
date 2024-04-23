package com.anshyeon.fashioncode.data.repository

import com.anshyeon.fashioncode.BuildConfig
import com.anshyeon.fashioncode.data.datastore.DataStore
import com.anshyeon.fashioncode.network.AdobeLoginApiClient
import com.anshyeon.fashioncode.network.DropboxApiClient
import com.anshyeon.fashioncode.network.extentions.onSuccess
import javax.inject.Inject

class TokenRepository @Inject constructor(
    private val adobeLoginApiClient: AdobeLoginApiClient,
    private val dropBoxApiClient: DropboxApiClient,
    private val dataStore: DataStore,
) {

    suspend fun saveImageToken(adobeToken: String?, dropBoxToken: String?) {
        dataStore.saveToken(adobeToken ?: "", dropBoxToken ?: "")
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
}