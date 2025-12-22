package com.sergi.tfg_app.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ScraperStatusResponse(
    val id: String,
    val keywords: String,
    val location: String,
    @Json(name = "num_offers") val numOffers: Int,
    val status: String,
    @Json(name = "progress_message") val progressMessage: String,
    @Json(name = "progress_percentage") val progressPercentage: Int,
    @Json(name = "jobs_scraped") val jobsScraped: Int,
    @Json(name = "error_message") val errorMessage: String?,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "completed_at") val completedAt: String?,
    @Json(name = "improved_cv_id") val improvedCvId: String?
)
