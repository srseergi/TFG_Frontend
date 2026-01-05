package com.sergi.tfg_app.data.remote.api

import com.sergi.tfg_app.data.remote.dto.GoogleLoginRequest
import com.sergi.tfg_app.data.remote.dto.LoginRequest
import com.sergi.tfg_app.data.remote.dto.LoginResponse
import com.sergi.tfg_app.data.remote.dto.RegisterRequest
import com.sergi.tfg_app.data.remote.dto.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("users/login/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("users/register/")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("users/google-login/")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): Response<LoginResponse>
}
