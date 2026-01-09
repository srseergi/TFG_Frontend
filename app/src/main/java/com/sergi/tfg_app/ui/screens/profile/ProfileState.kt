package com.sergi.tfg_app.ui.screens.profile

import com.sergi.tfg_app.util.AppLanguage

data class ProfileState(
    val username: String = "",
    val email: String = "",
    val selectedLanguage: AppLanguage = AppLanguage.ENGLISH,
    val isLoading: Boolean = true,
    val isDeleting: Boolean = false,
    val deleteError: String? = null
)
