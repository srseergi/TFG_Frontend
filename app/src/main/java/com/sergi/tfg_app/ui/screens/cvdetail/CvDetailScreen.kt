package com.sergi.tfg_app.ui.screens.cvdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sergi.tfg_app.ui.theme.BluePrimary
import com.sergi.tfg_app.ui.theme.GrayLight
import com.sergi.tfg_app.ui.theme.GrayMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CvDetailScreen(
    viewModel: CvDetailViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Dialog de error de descarga
    if (state.downloadState is DownloadState.Error) {
        AlertDialog(
            onDismissRequest = { viewModel.resetDownloadState() },
            title = { Text("Error") },
            text = { Text((state.downloadState as DownloadState.Error).message) },
            confirmButton = {
                TextButton(onClick = { viewModel.resetDownloadState() }) {
                    Text("Aceptar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.title.ifEmpty { "Detalle CV" }) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.error ?: "Error desconocido",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.retry() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            else -> {
                val feedback = state.feedback
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Información del CV con botón de descarga
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = GrayLight
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = state.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Posición: ${state.position}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                // Botón de descarga
                                Button(
                                    onClick = { viewModel.downloadAndOpenPdf(context) },
                                    enabled = state.downloadState !is DownloadState.Downloading,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (state.downloadState is DownloadState.Downloading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Descargando...")
                                    } else {
                                        Text("Ver PDF mejorado")
                                    }
                                }
                            }
                        }
                    }

                    // Seccion: Cumple
                    if (feedback != null && feedback.cumple.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Cumple",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        items(feedback.cumple) { item ->
                            FeedbackItem(text = item)
                        }
                    }

                    // Seccion: No Cumple
                    if (feedback != null && feedback.noCumple.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "No Cumple",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        items(feedback.noCumple) { item ->
                            FeedbackItem(text = item)
                        }
                    }

                    // Seccion: Recomendaciones
                    if (feedback != null && feedback.recomendaciones.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Recomendaciones",
                                color = BluePrimary
                            )
                        }
                        items(feedback.recomendaciones) { item ->
                            FeedbackItem(text = item)
                        }
                    }

                    // Espacio al final
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    color: androidx.compose.ui.graphics.Color
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun FeedbackItem(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = GrayMedium,
                        shape = MaterialTheme.shapes.small
                    )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
