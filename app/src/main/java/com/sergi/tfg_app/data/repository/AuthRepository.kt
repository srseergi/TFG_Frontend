package com.sergi.tfg_app.data.repository

import com.sergi.tfg_app.data.local.TokenDataStore
import com.sergi.tfg_app.data.remote.api.AuthApi
import com.sergi.tfg_app.data.remote.dto.LoginRequest
import com.sergi.tfg_app.data.remote.dto.RegisterRequest
import com.sergi.tfg_app.data.remote.dto.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AuthRepository(
    private val api: AuthApi,
    private val dataStore: TokenDataStore
) {

    suspend fun login(username: String, password: String): Result<User> {
        return try {
            val response = api.login(LoginRequest(username, password))

            if (response.isSuccessful) {
                val body = response.body()
                    ?: return Result.failure(Exception("Respuesta vacía del servidor"))
                dataStore.saveTokens(body.tokens.access, body.tokens.refresh)
                dataStore.saveUser(body.user.id, body.user.username, body.user.email)
                Result.success(body.user)
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Usuario o contraseña incorrectos"
                    else -> "Error de autenticación"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun register(username: String, email: String, password: String): Result<User> {
        return try {
            val response = api.register(RegisterRequest(username, email, password))

            if (response.isSuccessful) {
                val body = response.body()
                    ?: return Result.failure(Exception("Respuesta vacía del servidor"))
                dataStore.saveTokens(body.tokens.access, body.tokens.refresh)
                dataStore.saveUser(body.user.id, body.user.username, body.user.email)
                Result.success(body.user)
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "El usuario o email ya existe"
                    else -> "Error en el registro"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
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
}
