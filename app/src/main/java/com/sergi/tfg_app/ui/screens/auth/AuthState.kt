package com.sergi.tfg_app.ui.screens.auth

import com.sergi.tfg_app.data.remote.dto.User

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}
