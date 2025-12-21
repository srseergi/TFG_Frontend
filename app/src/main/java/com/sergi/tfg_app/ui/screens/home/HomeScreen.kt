package com.sergi.tfg_app.ui.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onProfileClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    // Intercepta el botón back y no hace nada
    BackHandler(enabled = true) {
        // No hacer nada, se queda en Home
    }

    var position by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subir CV") },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil"
                        )
                    }
                }
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
            OutlinedTextField(
                value = position,
                onValueChange = { position = it },
                label = { Text("Posicion deseada") },
                placeholder = { Text("Ej: Android Developer") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = { /* TODO: Seleccionar archivo */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Seleccionar archivo PDF")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* TODO: Subir CV */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Subir CV")
            }

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedButton(
                onClick = onGalleryClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver mis CVs")
            }
        }
    }
}
