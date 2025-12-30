package com.sergi.tfg_app.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.sergi.tfg_app.R
import com.sergi.tfg_app.ui.components.ErrorMessage
import com.sergi.tfg_app.ui.components.LanguagePickerDialog
import com.sergi.tfg_app.ui.screens.auth.AuthState
import com.sergi.tfg_app.util.AppLanguage
import com.sergi.tfg_app.util.LanguageManager
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onLanguageChange: suspend (String) -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showLanguageDialog by remember { mutableStateOf(false) }

    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currentLanguage by remember { mutableStateOf(LanguageManager.getCurrentLanguage(context)) }

    // Navegar cuando el login es exitoso
    LaunchedEffect(loginState) {
        if (loginState is AuthState.Success) {
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    // Language picker dialog
    if (showLanguageDialog) {
        LanguagePickerDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = { language ->
                scope.launch {
                    onLanguageChange(language.code)
                    LanguageManager.setLocale(context, language)
                    currentLanguage = language
                }
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.login_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(stringResource(R.string.username)) },
                singleLine = true,
                enabled = loginState !is AuthState.Loading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password)) },
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
                    Text(stringResource(R.string.login_button))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onRegisterClick,
                enabled = loginState !is AuthState.Loading
            ) {
                Text(stringResource(R.string.no_account_register))
            }
        }

        // Settings button in bottom right
        IconButton(
            onClick = { showLanguageDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.language_label),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
