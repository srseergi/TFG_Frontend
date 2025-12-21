package com.sergi.tfg_app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class TokenDataStore(private val context: Context) {

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USERNAME = stringPreferencesKey("username")
        private val EMAIL = stringPreferencesKey("email")
    }

    suspend fun saveTokens(access: String, refresh: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = access
            prefs[REFRESH_TOKEN] = refresh
        }
    }

    suspend fun saveUser(id: String, username: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = id
            prefs[USERNAME] = username
            prefs[EMAIL] = email
        }
    }

    fun getAccessToken(): Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[ACCESS_TOKEN]
    }

    fun getRefreshToken(): Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[REFRESH_TOKEN]
    }

    fun getUserId(): Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_ID]
    }

    fun getUsername(): Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USERNAME]
    }

    fun getEmail(): Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[EMAIL]
    }

    fun isLoggedIn(): Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ACCESS_TOKEN] != null
    }

    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
