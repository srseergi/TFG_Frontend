package com.sergi.tfg_app.data.repository

import com.sergi.tfg_app.data.local.TokenDataStore
import com.sergi.tfg_app.data.remote.api.AuthApi
import com.sergi.tfg_app.data.remote.dto.GoogleLoginRequest
import com.sergi.tfg_app.data.remote.dto.LoginRequest
import com.sergi.tfg_app.data.remote.dto.RegisterRequest
import com.sergi.tfg_app.data.remote.dto.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AuthRepository(
    private val api: AuthApi,
    private val dataStore: TokenDataStore
) {

    suspend fun loginWithGoogle(idToken: String): Result<User> {
        return try {
            val response = api.googleLogin(GoogleLoginRequest(idToken))

            if (response.isSuccessful) {
                val body = response.body()
                    ?: return Result.failure(Exception("Empty response from server"))
                dataStore.saveTokens(body.tokens.access, body.tokens.refresh)
                dataStore.saveUser(body.user.id, body.user.username, body.user.email)
                Result.success(body.user)
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Invalid Google token"
                    else -> "Google authentication error"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Connection error: ${e.message}"))
        }
    }

    suspend fun login(username: String, password: String): Result<User> {
        return try {
            val response = api.login(LoginRequest(username, password))

            if (response.isSuccessful) {
                val body = response.body()
                    ?: return Result.failure(Exception("Empty response from server"))
                dataStore.saveTokens(body.tokens.access, body.tokens.refresh)
                dataStore.saveUser(body.user.id, body.user.username, body.user.email)
                Result.success(body.user)
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Invalid username or password"
                    else -> "Authentication error"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Connection error: ${e.message}"))
        }
    }

    suspend fun register(username: String, email: String, password: String): Result<User> {
        return try {
            val response = api.register(RegisterRequest(username, email, password))

            if (response.isSuccessful) {
                val body = response.body()
                    ?: return Result.failure(Exception("Empty response from server"))
                dataStore.saveTokens(body.tokens.access, body.tokens.refresh)
                dataStore.saveUser(body.user.id, body.user.username, body.user.email)
                Result.success(body.user)
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Username or email already exists"
                    else -> "Registration error"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Connection error: ${e.message}"))
        }
    }

    suspend fun logout() {
        dataStore.clear()
    }

    suspend fun getAccessTokenOnce(): String? {
        return dataStore.getAccessToken().first()
    }

    suspend fun getRefreshTokenOnce(): String? {
        return dataStore.getRefreshToken().first()
    }

    fun isLoggedIn(): Flow<Boolean> = dataStore.isLoggedIn()

    fun getAccessToken(): Flow<String?> = dataStore.getAccessToken()

    fun getUsername(): Flow<String?> = dataStore.getUsername()

    fun getEmail(): Flow<String?> = dataStore.getEmail()

    fun getLanguage(): Flow<String?> = dataStore.getLanguage()

    suspend fun saveLanguage(languageCode: String) {
        dataStore.saveLanguage(languageCode)
    }
}
