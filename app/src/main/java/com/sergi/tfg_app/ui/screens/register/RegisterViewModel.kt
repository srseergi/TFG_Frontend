package com.sergi.tfg_app.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergi.tfg_app.data.repository.AuthRepository
import com.sergi.tfg_app.ui.screens.auth.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState

    fun register(username: String, email: String, password: String) {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _registerState.value = AuthState.Error("Completa todos los campos")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _registerState.value = AuthState.Error("Email no válido")
            return
        }

        if (password.length < 8) {
            _registerState.value = AuthState.Error("La contraseña debe tener al menos 8 caracteres")
            return
        }

        viewModelScope.launch {
            _registerState.value = AuthState.Loading

            repository.register(username, email, password)
                .onSuccess { user ->
                    _registerState.value = AuthState.Success(user)
                }
                .onFailure { error ->
                    _registerState.value = AuthState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun resetState() {
        _registerState.value = AuthState.Idle
    }
}
