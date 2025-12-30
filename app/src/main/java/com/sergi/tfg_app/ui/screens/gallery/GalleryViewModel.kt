package com.sergi.tfg_app.ui.screens.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergi.tfg_app.data.repository.CvRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GalleryViewModel(
    private val cvRepository: CvRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GalleryState())
    val state: StateFlow<GalleryState> = _state

    init {
        loadCvList()
    }

    fun loadCvList() {
        viewModelScope.launch {
            _state.value = GalleryState(isLoading = true)

            cvRepository.listCvs()
                .onSuccess { cvList ->
                    _state.value = GalleryState(
                        isLoading = false,
                        cvList = cvList
                    )
                }
                .onFailure { error ->
                    _state.value = GalleryState(
                        isLoading = false,
                        error = error.message ?: "Error al cargar los CVs"
                    )
                }
        }
    }

    fun retry() {
        loadCvList()
    }
}
