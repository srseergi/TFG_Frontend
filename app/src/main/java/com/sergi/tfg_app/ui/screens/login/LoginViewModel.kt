package com.sergi.tfg_app.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergi.tfg_app.data.repository.AuthRepository
import com.sergi.tfg_app.ui.screens.auth.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = AuthState.Error("Completa todos los campos")
            return
        }

        viewModelScope.launch {
            _loginState.value = AuthState.Loading

            repository.login(username, password)
                .onSuccess { user ->
                    _loginState.value = AuthState.Success(user)
                }
                .onFailure { error ->
                    _loginState.value = AuthState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun resetState() {
        _loginState.value = AuthState.Idle
    }
}
