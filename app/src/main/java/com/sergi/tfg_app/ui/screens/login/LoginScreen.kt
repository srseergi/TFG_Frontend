package com.sergi.tfg_app.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.sergi.tfg_app.ui.components.ErrorMessage
import com.sergi.tfg_app.ui.screens.auth.AuthState

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginState by viewModel.loginState.collectAsState()

    // Navegar cuando el login es exitoso
    LaunchedEffect(loginState) {
        if (loginState is AuthState.Success) {
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "TFG App",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
            singleLine = true,
            enabled = loginState !is AuthState.Loading,
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
            enabled = loginState !is AuthState.Loading,
            modifier = Modifier.fillMaxWidth()
        )

        // Mostrar error si existe
        if (loginState is AuthState.Error) {
            ErrorMessage(message = (loginState as AuthState.Error).message)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.login(username, password) },
            enabled = loginState !is AuthState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loginState is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Iniciar sesión")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onRegisterClick,
            enabled = loginState !is AuthState.Loading
        ) {
            Text("No tienes cuenta? Regístrate")
        }
    }
}
