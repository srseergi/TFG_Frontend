package com.sergi.tfg_app.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CvFeedback(
    val cumple: List<String>,
    @Json(name = "no_cumple") val noCumple: List<String>,
    val recomendaciones: List<String>
)
