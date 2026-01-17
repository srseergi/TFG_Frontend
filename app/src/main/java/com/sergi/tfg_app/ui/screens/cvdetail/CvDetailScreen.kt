package com.sergi.tfg_app.ui.screens.cvdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sergi.tfg_app.R
import com.sergi.tfg_app.ui.theme.InfoBlue
import com.sergi.tfg_app.ui.theme.Stone100
import com.sergi.tfg_app.ui.theme.Stone300

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CvDetailScreen(
    viewModel: CvDetailViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    if (state.downloadState is DownloadState.Error) {
        AlertDialog(
            onDismissRequest = { viewModel.resetDownloadState() },
            title = { Text(stringResource(R.string.error)) },
            text = { Text((state.downloadState as DownloadState.Error).message) },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.resetDownloadState() },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(stringResource(R.string.accept))
                }
            }
        )
    }

    if (state.originalDownloadState is DownloadState.Error) {
        AlertDialog(
            onDismissRequest = { viewModel.resetOriginalDownloadState() },
            title = { Text(stringResource(R.string.error)) },
            text = { Text((state.originalDownloadState as DownloadState.Error).message) },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.resetOriginalDownloadState() },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(stringResource(R.string.accept))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.title.ifEmpty { stringResource(R.string.cv_detail_title) }) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
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
                            text = state.error ?: stringResource(R.string.unknown_error),
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.retry() },
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(stringResource(R.string.retry))
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
                    item {
                        CvCard(
                            title = state.title,
                            position = state.position,
                            buttonText = if (state.downloadState is DownloadState.Downloading) {
                                stringResource(R.string.downloading)
                            } else {
                                stringResource(R.string.view_improved_pdf)
                            },
                            isDownloading = state.downloadState is DownloadState.Downloading,
                            onDownloadClick = { viewModel.downloadAndOpenPdf(context) }
                        )
                    }

                    item {
                        SectionHeader(
                            title = stringResource(R.string.original_cv_title),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        CvCard(
                            title = state.originalTitle,
                            position = state.originalPosition,
                            buttonText = if (state.originalDownloadState is DownloadState.Downloading) {
                                stringResource(R.string.downloading)
                            } else {
                                stringResource(R.string.view_original_pdf)
                            },
                            isDownloading = state.originalDownloadState is DownloadState.Downloading,
                            onDownloadClick = { viewModel.downloadAndOpenOriginalPdf(context) }
                        )
                    }

                    if (feedback != null && feedback.cumple.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = stringResource(R.string.section_meets),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        items(feedback.cumple) { item ->
                            FeedbackItem(text = item)
                        }
                    }

                    if (feedback != null && feedback.noCumple.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = stringResource(R.string.section_does_not_meet),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        items(feedback.noCumple) { item ->
                            FeedbackItem(text = item)
                        }
                    }

                    if (feedback != null && feedback.recomendaciones.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = stringResource(R.string.section_recommendations),
                                color = InfoBlue
                            )
                        }
                        items(feedback.recomendaciones) { item ->
                            FeedbackItem(text = item)
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CvCard(
    title: String,
    position: String,
    buttonText: String,
    isDownloading: Boolean,
    onDownloadClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Stone100
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.position_label, position),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDownloadClick,
                enabled = !isDownloading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp)
            ) {
                if (isDownloading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(buttonText)
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
                        color = Stone300,
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
