package com.sergi.tfg_app.ui.screens.home

sealed class HomeState {
    object Idle : HomeState()
    object Uploading : HomeState()
    data class Polling(
        val percentage: Int,
        val message: String
    ) : HomeState()
    data class Success(val improvedCvId: String) : HomeState()
    data class Error(val message: String) : HomeState()
}
