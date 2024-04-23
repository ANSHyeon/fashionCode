package com.anshyeon.fashioncode.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.anshyeon.fashioncode.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object PreferenceKeys {
        val ADOBE_ACCESS_TOKEN = stringPreferencesKey("adobe_access_token")
        val DROPBOX_ACCESS_TOKEN = stringPreferencesKey("dropbox_access_token")
    }

    private val Context.adobeTokenDataStore by preferencesDataStore(Constants.ADOBE_TOKEN_DATASTORE_NAME)
    private val Context.dropBoxTokenDataStore by preferencesDataStore(Constants.DROPBOX_TOKEN_DATASTORE_NAME)

    suspend fun saveAdobeToken(adobeToken: String) {
        context.adobeTokenDataStore.edit { prefs ->
            prefs[PreferenceKeys.ADOBE_ACCESS_TOKEN] = adobeToken
        }
    }

    suspend fun saveDropBoxToken(dropBoxToken: String) {
        context.dropBoxTokenDataStore.edit { prefs ->
            prefs[PreferenceKeys.DROPBOX_ACCESS_TOKEN] = dropBoxToken
        }
    }

    suspend fun getAdobeToken(): Flow<String> {
        return context.adobeTokenDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    exception.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { prefs ->
                prefs[PreferenceKeys.ADOBE_ACCESS_TOKEN] ?: ""
            }
    }

    suspend fun getDropBoxToken(): Flow<String> {
        return context.dropBoxTokenDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    exception.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { prefs ->
                prefs[PreferenceKeys.DROPBOX_ACCESS_TOKEN] ?: ""
            }
    }
}
