package com.sergi.tfg_app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sergi.tfg_app.R
import com.sergi.tfg_app.util.AppLanguage

@Composable
fun LanguagePickerDialog(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.language_label)) },
        text = {
            Column {
                AppLanguage.entries.forEach { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLanguageSelected(language)
                                onDismiss()
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = language == currentLanguage,
                            onClick = {
                                onLanguageSelected(language)
                                onDismiss()
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = getLanguageDisplayName(language),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
private fun getLanguageDisplayName(language: AppLanguage): String {
    return when (language) {
        AppLanguage.ENGLISH -> stringResource(R.string.language_english)
        AppLanguage.SPANISH -> stringResource(R.string.language_spanish)
        AppLanguage.CATALAN -> stringResource(R.string.language_catalan)
    }
}
