package com.sergi.tfg_app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergi.tfg_app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            // Combina los dos Flows (username y email) en uno solo
            repository.getUsername()
                .combine(repository.getEmail()) { username, email ->
                    ProfileState(
                        username = username ?: "",
                        email = email ?: "",
                        isLoading = false
                    )
                }
                .collect { state ->
                    _profileState.value = state
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
