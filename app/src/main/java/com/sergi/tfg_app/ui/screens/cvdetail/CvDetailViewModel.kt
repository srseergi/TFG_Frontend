package com.sergi.tfg_app.ui.screens.cvdetail

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergi.tfg_app.data.repository.CvRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class CvDetailViewModel(
    private val cvRepository: CvRepository,
    private val cvId: String
) : ViewModel() {

    private val _state = MutableStateFlow(CvDetailState())
    val state: StateFlow<CvDetailState> = _state

    private val httpClient = OkHttpClient()

    init {
        loadCvDetails()
    }

    private fun loadCvDetails() {
        viewModelScope.launch {
            _state.value = CvDetailState(isLoading = true)

            cvRepository.getImprovedCv(cvId)
                .onSuccess { cv ->
                    _state.value = CvDetailState(
                        isLoading = false,
                        title = cv.title,
                        position = cv.position,
                        pdfFileUrl = cv.pdfFileUrl,
                        feedback = cv.feedback
                    )
                }
                .onFailure { error ->
                    _state.value = CvDetailState(
                        isLoading = false,
                        error = error.message ?: "Error al cargar el CV"
                    )
                }
        }
    }

    fun downloadAndOpenPdf(context: Context) {
        val pdfUrl = _state.value.pdfFileUrl
        if (pdfUrl.isBlank()) {
            _state.value = _state.value.copy(
                downloadState = DownloadState.Error("URL del PDF no disponible")
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(downloadState = DownloadState.Downloading)

            try {
                val pdfFile = downloadPdf(context, pdfUrl)
                if (pdfFile != null) {
                    _state.value = _state.value.copy(downloadState = DownloadState.Success)
                    openPdfFile(context, pdfFile)
                } else {
                    _state.value = _state.value.copy(
                        downloadState = DownloadState.Error("Error al descargar el PDF")
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    downloadState = DownloadState.Error(e.message ?: "Error desconocido")
                )
            }
        }
    }

    private suspend fun downloadPdf(context: Context, url: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(url)
                    .build()

                val response = httpClient.newCall(request).execute()

                if (!response.isSuccessful) {
                    return@withContext null
                }

                val pdfDir = File(context.cacheDir, "pdfs")
                if (!pdfDir.exists()) {
                    pdfDir.mkdirs()
                }

                val fileName = "cv_${cvId}.pdf"
                val pdfFile = File(pdfDir, fileName)

                response.body?.byteStream()?.use { inputStream ->
                    FileOutputStream(pdfFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                pdfFile
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun openPdfFile(context: Context, file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(Intent.createChooser(intent, "Abrir PDF con..."))
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                downloadState = DownloadState.Error("No se encontró una app para abrir PDFs")
            )
        }
    }

    fun resetDownloadState() {
        _state.value = _state.value.copy(downloadState = DownloadState.Idle)
    }

    fun retry() {
        loadCvDetails()
    }
}
