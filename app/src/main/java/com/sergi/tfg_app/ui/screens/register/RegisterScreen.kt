package com.sergi.tfg_app.ui.screens.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.sergi.tfg_app.ui.components.ErrorMessage
import com.sergi.tfg_app.ui.screens.auth.AuthState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val registerState by viewModel.registerState.collectAsState()

    // Navegar cuando el registro es exitoso
    LaunchedEffect(registerState) {
        if (registerState is AuthState.Success) {
            onRegisterSuccess()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro") },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        enabled = registerState !is AuthState.Loading
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario") },
                singleLine = true,
                enabled = registerState !is AuthState.Loading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                enabled = registerState !is AuthState.Loading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                enabled = registerState !is AuthState.Loading,
                modifier = Modifier.fillMaxWidth()
            )

            // Mostrar error si existe
            if (registerState is AuthState.Error) {
                ErrorMessage(message = (registerState as AuthState.Error).message)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.register(username, email, password) },
                enabled = registerState !is AuthState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (registerState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Registrarse")
                }
            }
        }
    }
}
