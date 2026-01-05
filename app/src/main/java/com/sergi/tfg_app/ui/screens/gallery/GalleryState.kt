package com.sergi.tfg_app.ui.screens.gallery

import com.sergi.tfg_app.data.remote.dto.CvListItem

data class GalleryState(
    val isLoading: Boolean = true,
    val cvList: List<CvListItem> = emptyList(),
    val error: String? = null,
    val searchQuery: String = ""
)
