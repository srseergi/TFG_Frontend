package com.sergi.tfg_app.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Tokens(
    val access: String,
    val refresh: String
)
