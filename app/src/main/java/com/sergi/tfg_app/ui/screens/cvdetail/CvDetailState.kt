package com.sergi.tfg_app.ui.screens.cvdetail

import com.sergi.tfg_app.data.remote.dto.CvFeedback

data class CvDetailState(
    val isLoading: Boolean = true,
    val title: String = "",
    val position: String = "",
    val pdfFileUrl: String = "",
    val feedback: CvFeedback? = null,
    val error: String? = null,
    val downloadState: DownloadState = DownloadState.Idle,
    val originalTitle: String = "",
    val originalPosition: String = "",
    val originalPdfFileUrl: String = "",
    val originalDownloadState: DownloadState = DownloadState.Idle
)

sealed class DownloadState {
    object Idle : DownloadState()
    object Downloading : DownloadState()
    object Success : DownloadState()
    data class Error(val message: String) : DownloadState()
}
