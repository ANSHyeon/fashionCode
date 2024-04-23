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

    private val Context.tokenDataStore by preferencesDataStore(Constants.TOKEN_DATASTORE_NAME)

    suspend fun saveToken(adobeToken: String, dropBoxToken: String) {
        context.tokenDataStore.edit { prefs ->
            prefs[PreferenceKeys.ADOBE_ACCESS_TOKEN] = adobeToken
            prefs[PreferenceKeys.DROPBOX_ACCESS_TOKEN] = dropBoxToken
        }
    }

    suspend fun getToken(): Flow<List<String>> {
        return context.tokenDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    exception.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { prefs ->
                prefs.asMap().values.toList().map {
                    it.toString()
                }
            }
    }
}
