package com.onehub.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("onehub_session")

class SessionStore(private val context: Context) {
    private val accessKey = stringPreferencesKey("access_token")
    private val refreshKey = stringPreferencesKey("refresh_token")
    private val userJsonKey = stringPreferencesKey("user_json")

    val accessToken: Flow<String?> = context.dataStore.data.map { it[accessKey] }

    suspend fun saveSession(access: String, refresh: String, userJson: String) {
        context.dataStore.edit {
            it[accessKey] = access
            it[refreshKey] = refresh
            it[userJsonKey] = userJson
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
