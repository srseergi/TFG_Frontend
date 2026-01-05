package com.sergi.tfg_app.ui.screens.login

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.sergi.tfg_app.BuildConfig
import com.sergi.tfg_app.R
import com.sergi.tfg_app.ui.components.ErrorMessage
import com.sergi.tfg_app.ui.components.LanguagePickerDialog
import com.sergi.tfg_app.ui.screens.auth.AuthState
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

    val googleSignInErrorText = stringResource(R.string.google_sign_in_error)
    val googleSignInCancelledText = stringResource(R.string.google_sign_in_cancelled)

    LaunchedEffect(loginState) {
        if (loginState is AuthState.Success) {
            onLoginSuccess()
            viewModel.resetState()
        }
    }

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.or_divider),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    scope.launch {
                        signInWithGoogle(
                            context = context as Activity,
                            onSuccess = { idToken ->
                                viewModel.loginWithGoogle(idToken)
                            },
                            onError = { message ->
                                viewModel.setError(message)
                            },
                            errorText = googleSignInErrorText,
                            cancelledText = googleSignInCancelledText
                        )
                    }
                },
                enabled = loginState !is AuthState.Loading,
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.sign_in_with_google))
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onRegisterClick,
                enabled = loginState !is AuthState.Loading
            ) {
                Text(stringResource(R.string.no_account_register))
            }
        }

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

private suspend fun signInWithGoogle(
    context: Activity,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit,
    errorText: String,
    cancelledText: String
) {
    val credentialManager = CredentialManager.create(context)

    val signInOption = GetSignInWithGoogleOption.Builder(BuildConfig.GOOGLE_WEB_CLIENT_ID)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(signInOption)
        .build()

    try {
        val result = credentialManager.getCredential(
            request = request,
            context = context
        )
        handleSignInResult(result, onSuccess, onError, errorText)
    } catch (e: GetCredentialCancellationException) {
        onError(cancelledText)
    } catch (e: GetCredentialException) {
        onError("${e.type}: ${e.message}")
    }
}

private fun handleSignInResult(
    result: GetCredentialResponse,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit,
    errorText: String
) {
    when (val credential = result.credential) {
        is CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    onSuccess(googleIdTokenCredential.idToken)
                } catch (e: GoogleIdTokenParsingException) {
                    onError(errorText)
                }
            } else {
                onError(errorText)
            }
        }
        else -> {
            onError(errorText)
        }
    }
}
