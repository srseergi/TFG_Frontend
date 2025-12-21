package com.sergi.tfg_app.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val message: String,
    val user: User,
    val tokens: Tokens
)
