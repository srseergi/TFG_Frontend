package com.sergi.tfg_app.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImprovedCvResponse(
    val id: String,
    val user: User,
    val title: String,
    val position: String,
    @Json(name = "pdf_file_url") val pdfFileUrl: String,
    @Json(name = "original_filename") val originalFilename: String,
    val feedback: CvFeedback,
    @Json(name = "created_at") val createdAt: String
)
