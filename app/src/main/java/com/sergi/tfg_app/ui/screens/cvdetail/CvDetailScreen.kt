package com.sergi.tfg_app.ui.screens.cvdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sergi.tfg_app.ui.theme.BluePrimary
import com.sergi.tfg_app.ui.theme.GrayLight
import com.sergi.tfg_app.ui.theme.GrayMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CvDetailScreen(
    cvTitle: String,
    onBackClick: () -> Unit
) {
    // Datos placeholder
    val cumpleList = listOf(
        "Experiencia relevante en el sector",
        "Formacion academica adecuada",
        "Habilidades tecnicas requeridas"
    )

    val noCumpleList = listOf(
        "Falta certificacion especifica",
        "No menciona trabajo en equipo"
    )

    val recomendacionesList = listOf(
        "Anadir proyectos personales al portfolio",
        "Incluir metricas de logros conseguidos",
        "Mejorar seccion de idiomas",
        "Considerar anadir referencias"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cvTitle) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagen placeholder del CV
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = GrayLight
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Vista previa del CV",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Seccion: Cumple
            item {
                SectionHeader(
                    title = "Cumple",
                    color = MaterialTheme.colorScheme.primary
                )
            }
            items(cumpleList) { item ->
                FeedbackItem(text = item)
            }

            // Seccion: No Cumple
            item {
                SectionHeader(
                    title = "No Cumple",
                    color = MaterialTheme.colorScheme.error
                )
            }
            items(noCumpleList) { item ->
                FeedbackItem(text = item)
            }

            // Seccion: Recomendaciones
            item {
                SectionHeader(
                    title = "Recomendaciones",
                    color = BluePrimary
                )
            }
            items(recomendacionesList) { item ->
                FeedbackItem(text = item)
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
