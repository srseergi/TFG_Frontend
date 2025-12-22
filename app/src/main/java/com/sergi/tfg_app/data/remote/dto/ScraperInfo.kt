package com.sergi.tfg_app.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ScraperInfo(
    @Json(name = "scraper_id") val scraperId: String,
    val status: String,
    @Json(name = "status_url") val statusUrl: String,
    val message: String
)
