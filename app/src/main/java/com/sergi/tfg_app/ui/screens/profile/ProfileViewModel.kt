package com.sergi.tfg_app.ui.screens.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergi.tfg_app.data.remote.api.UserApi
import com.sergi.tfg_app.data.repository.AuthRepository
import com.sergi.tfg_app.util.AppLanguage
import com.sergi.tfg_app.util.LanguageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: AuthRepository,
    private val userApi: UserApi
) : ViewModel() {

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState

    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess: StateFlow<Boolean> = _deleteSuccess

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            combine(
                repository.getUsername(),
                repository.getEmail(),
                repository.getLanguage()
            ) { username, email, languageCode ->
                ProfileState(
                    username = username ?: "",
                    email = email ?: "",
                    selectedLanguage = AppLanguage.fromCode(languageCode ?: "en"),
                    isLoading = false
                )
            }.collect { state ->
                _profileState.value = state
            }
        }
    }

    fun changeLanguage(context: Context, language: AppLanguage) {
        viewModelScope.launch {
            repository.saveLanguage(language.code)
            LanguageManager.setLocale(context, language)
            _profileState.value = _profileState.value.copy(selectedLanguage = language)
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isDeleting = true, deleteError = null)

            try {
                val response = userApi.deleteAccount()
                if (response.isSuccessful) {
                    repository.logout()
                    _deleteSuccess.value = true
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody ?: "Error deleting account"
                    _profileState.value = _profileState.value.copy(
                        isDeleting = false,
                        deleteError = errorMessage
                    )
                }
            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(
                    isDeleting = false,
                    deleteError = "Connection error: ${e.message}"
                )
            }
        }
    }

    fun clearDeleteError() {
        _profileState.value = _profileState.value.copy(deleteError = null)
    }

    fun clearDeleteSuccess() {
        _deleteSuccess.value = false
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
