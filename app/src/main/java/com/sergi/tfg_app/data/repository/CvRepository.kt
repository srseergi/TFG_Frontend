package com.sergi.tfg_app.data.repository

import android.content.Context
import android.net.Uri
import com.sergi.tfg_app.data.local.TokenDataStore
import com.sergi.tfg_app.data.remote.api.CvApi
import com.sergi.tfg_app.data.remote.dto.CreateCvResponse
import com.sergi.tfg_app.data.remote.dto.CvListItem
import com.sergi.tfg_app.data.remote.dto.ImprovedCvResponse
import com.sergi.tfg_app.data.remote.dto.ScraperStatusResponse
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class CvRepository(
    private val api: CvApi,
    private val dataStore: TokenDataStore,
    private val context: Context
) {

    suspend fun uploadCv(
        pdfUri: Uri,
        title: String,
        position: String
    ): Result<CreateCvResponse> {
        return try {
            val pdfFile = uriToFile(pdfUri)
                ?: return Result.failure(Exception("No se pudo leer el archivo PDF"))

            val pdfRequestBody = pdfFile.asRequestBody("application/pdf".toMediaTypeOrNull())
            val pdfPart = MultipartBody.Part.createFormData("pdf_file", pdfFile.name, pdfRequestBody)

            val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val positionBody = position.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = api.createBaseCv(pdfPart, titleBody, positionBody)

            if (response.isSuccessful) {
                val body = response.body()
                    ?: return Result.failure(Exception("Respuesta vacía del servidor"))

                // Guardar el scraperId para poder reanudar si se cierra la app
                dataStore.saveScraperId(body.scraper.scraperId)

                Result.success(body)
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Archivo no válido o datos incorrectos"
                    401 -> "Sesión expirada, inicia sesión de nuevo"
                    413 -> "El archivo es demasiado grande (máx. 10MB)"
                    else -> "Error al subir el CV: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun getScraperStatus(scraperId: String): Result<ScraperStatusResponse> {
        return try {
            val response = api.getScraperStatus(scraperId)

            if (response.isSuccessful) {
                val body = response.body()
                    ?: return Result.failure(Exception("Respuesta vacía del servidor"))
                Result.success(body)
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "Proceso no encontrado"
                    401 -> "Sesión expirada"
                    else -> "Error al obtener estado: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun getImprovedCv(cvId: String): Result<ImprovedCvResponse> {
        return try {
            val response = api.getImprovedCv(cvId)

            if (response.isSuccessful) {
                val body = response.body()
                    ?: return Result.failure(Exception("Respuesta vacía del servidor"))
                Result.success(body)
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "CV no encontrado"
                    401 -> "Sesión expirada"
                    else -> "Error al obtener CV: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun getActiveScraperId(): String? {
        return dataStore.getScraperId().first()
    }

    suspend fun clearScraperId() {
        dataStore.clearScraperId()
    }

    suspend fun listCvs(): Result<List<CvListItem>> {
        return try {
            val response = api.listImprovedCvs()

            if (response.isSuccessful) {
                val body = response.body()
                    ?: return Result.failure(Exception("Respuesta vacía del servidor"))
                Result.success(body)
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Sesión expirada"
                    else -> "Error al obtener la lista: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    private fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val fileName = getFileName(uri) ?: "temp_cv.pdf"
            val tempFile = File(context.cacheDir, fileName)

            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()

            tempFile
        } catch (e: Exception) {
            null
        }
    }

    private fun getFileName(uri: Uri): String? {
        var fileName: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) {
                fileName = cursor.getString(nameIndex)
            }
        }
        return fileName
    }
}
