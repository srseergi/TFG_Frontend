package com.sergi.tfg_app.ui.screens.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sergi.tfg_app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onCvReady: (String) -> Unit
) {
    val homeState by viewModel.homeState.collectAsState()
    val selectedFileName by viewModel.selectedFileName.collectAsState()
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedUri = uri
        if (uri != null) {
            val fileName = getFileName(context, uri)
            viewModel.setSelectedFile(fileName)
        } else {
            viewModel.setSelectedFile(null)
        }
    }

    LaunchedEffect(homeState) {
        if (homeState is HomeState.Success) {
            val cvId = (homeState as HomeState.Success).improvedCvId
            viewModel.resetState()
            onCvReady(cvId)
        }
    }

    if (homeState is HomeState.Error) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissError() },
            title = { Text(stringResource(R.string.error)) },
            text = { Text((homeState as HomeState.Error).message) },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissError() }) {
                    Text(stringResource(R.string.accept))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.home_title)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = homeState) {
                is HomeState.Idle, is HomeState.Error -> {
                    UploadForm(
                        title = title,
                        onTitleChange = { title = it },
                        position = position,
                        onPositionChange = { position = it },
                        selectedFileName = selectedFileName,
                        onSelectFile = { pdfLauncher.launch("application/pdf") },
                        onSubmit = {
                            selectedUri?.let { uri ->
                                viewModel.uploadCv(uri, title, position)
                            }
                        },
                        isEnabled = true
                    )
                }

                is HomeState.Uploading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(stringResource(R.string.uploading_cv))
                        }
                    }
                }

                is HomeState.Polling -> {
                    PollingView(
                        percentage = state.percentage,
                        message = state.message
                    )
                }

                is HomeState.Success -> { }
            }
        }
    }
}

@Composable
private fun UploadForm(
    title: String,
    onTitleChange: (String) -> Unit,
    position: String,
    onPositionChange: (String) -> Unit,
    selectedFileName: String?,
    onSelectFile: () -> Unit,
    onSubmit: () -> Unit,
    isEnabled: Boolean
) {
    OutlinedTextField(
        value = title,
        onValueChange = onTitleChange,
        label = { Text(stringResource(R.string.cv_title_label)) },
        placeholder = { Text(stringResource(R.string.cv_title_placeholder)) },
        singleLine = true,
        enabled = isEnabled,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = position,
        onValueChange = onPositionChange,
        label = { Text(stringResource(R.string.desired_position_label)) },
        placeholder = { Text(stringResource(R.string.desired_position_placeholder)) },
        singleLine = true,
        enabled = isEnabled,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(24.dp))

    OutlinedButton(
        onClick = onSelectFile,
        enabled = isEnabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(selectedFileName ?: stringResource(R.string.select_pdf_file))
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onSubmit,
        enabled = isEnabled && selectedFileName != null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.improve_cv_button))
    }
}

@Composable
private fun PollingView(
    percentage: Int,
    message: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.improving_cv),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.polling_info_message),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

private fun getFileName(context: android.content.Context, uri: Uri): String? {
    var fileName: String? = null
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        if (nameIndex >= 0 && cursor.moveToFirst()) {
            fileName = cursor.getString(nameIndex)
        }
    }
    return fileName
}
