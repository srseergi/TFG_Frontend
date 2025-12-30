package com.sergi.tfg_app.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CvListItem(
    val id: String,
    val title: String,
    val position: String,
    @Json(name = "created_at") val createdAt: String
)
