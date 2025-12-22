package com.sergi.tfg_app.ui.screens.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergi.tfg_app.data.repository.CvRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val cvRepository: CvRepository
) : ViewModel() {

    private val _homeState = MutableStateFlow<HomeState>(HomeState.Idle)
    val homeState: StateFlow<HomeState> = _homeState

    private val _selectedFileName = MutableStateFlow<String?>(null)
    val selectedFileName: StateFlow<String?> = _selectedFileName

    private var pollingJob: Job? = null

    companion object {
        private const val POLLING_INTERVAL_MS = 3000L
        private const val STATUS_PENDING = "PENDING"
        private const val STATUS_PROCESSING = "PROCESSING"
        private const val STATUS_SUCCESS = "SUCCESS"
        private const val STATUS_FAILURE = "FAILURE"
    }

    init {
        checkForActivePolling()
    }

    private fun checkForActivePolling() {
        viewModelScope.launch {
            val activeScraperId = cvRepository.getActiveScraperId()
            if (activeScraperId != null) {
                // Hay un proceso activo, consultar su estado
                checkAndResumePolling(activeScraperId)
            }
        }
    }

    private suspend fun checkAndResumePolling(scraperId: String) {
        cvRepository.getScraperStatus(scraperId)
            .onSuccess { status ->
                when (status.status) {
                    STATUS_SUCCESS -> {
                        // Ya terminó, navegar al resultado
                        val improvedCvId = status.improvedCvId
                        if (improvedCvId != null) {
                            cvRepository.clearScraperId()
                            _homeState.value = HomeState.Success(improvedCvId)
                        } else {
                            cvRepository.clearScraperId()
                            _homeState.value = HomeState.Error("CV mejorado no encontrado")
                        }
                    }
                    STATUS_FAILURE -> {
                        cvRepository.clearScraperId()
                        _homeState.value = HomeState.Error(
                            status.errorMessage ?: "El proceso ha fallado"
                        )
                    }
                    STATUS_PENDING, STATUS_PROCESSING -> {
                        // Continuar polling
                        _homeState.value = HomeState.Polling(
                            percentage = status.progressPercentage,
                            message = status.progressMessage
                        )
                        startPolling(scraperId)
                    }
                    else -> {
                        cvRepository.clearScraperId()
                        _homeState.value = HomeState.Idle
                    }
                }
            }
            .onFailure { error ->
                cvRepository.clearScraperId()
                _homeState.value = HomeState.Error(error.message ?: "Error desconocido")
            }
    }

    fun setSelectedFile(fileName: String?) {
        _selectedFileName.value = fileName
    }

    fun uploadCv(pdfUri: Uri, title: String, position: String) {
        if (title.isBlank()) {
            _homeState.value = HomeState.Error("El título es obligatorio")
            return
        }
        if (position.isBlank()) {
            _homeState.value = HomeState.Error("La posición es obligatoria")
            return
        }
        if (_selectedFileName.value == null) {
            _homeState.value = HomeState.Error("Selecciona un archivo PDF")
            return
        }

        viewModelScope.launch {
            _homeState.value = HomeState.Uploading

            cvRepository.uploadCv(pdfUri, title, position)
                .onSuccess { response ->
                    _homeState.value = HomeState.Polling(
                        percentage = 0,
                        message = "Iniciando proceso..."
                    )
                    startPolling(response.scraper.scraperId)
                }
                .onFailure { error ->
                    _homeState.value = HomeState.Error(error.message ?: "Error al subir el CV")
                }
        }
    }

    private fun startPolling(scraperId: String) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(POLLING_INTERVAL_MS)

                cvRepository.getScraperStatus(scraperId)
                    .onSuccess { status ->
                        when (status.status) {
                            STATUS_SUCCESS -> {
                                val improvedCvId = status.improvedCvId
                                if (improvedCvId != null) {
                                    cvRepository.clearScraperId()
                                    _homeState.value = HomeState.Success(improvedCvId)
                                    pollingJob?.cancel()
                                    return@launch
                                }
                            }
                            STATUS_FAILURE -> {
                                cvRepository.clearScraperId()
                                _homeState.value = HomeState.Error(
                                    status.errorMessage ?: "El proceso ha fallado"
                                )
                                pollingJob?.cancel()
                                return@launch
                            }
                            STATUS_PENDING, STATUS_PROCESSING -> {
                                _homeState.value = HomeState.Polling(
                                    percentage = status.progressPercentage,
                                    message = status.progressMessage
                                )
                            }
                        }
                    }
                    .onFailure { error ->
                        // Error de red, continuar intentando
                        _homeState.value = HomeState.Polling(
                            percentage = (_homeState.value as? HomeState.Polling)?.percentage ?: 0,
                            message = "Reconectando..."
                        )
                    }
            }
        }
    }

    fun resetState() {
        _homeState.value = HomeState.Idle
        _selectedFileName.value = null
    }

    fun dismissError() {
        _homeState.value = HomeState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
