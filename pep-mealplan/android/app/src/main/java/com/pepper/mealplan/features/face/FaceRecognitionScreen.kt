package com.pepper.mealplan.features.face

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pepper.mealplan.RoboterActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun FaceRecognitionScreen(
    onAuthenticationSuccess: (String) -> Unit,
    viewModel: FaceRecognitionViewModel = viewModel()
) {
    // Dev-Schalter: auf true setzen, um Gesichtserkennung zu überspringen
    val devModeSkipFaceRecognition = true

    // State für Human Awareness Monitoring
    var isMonitoring by remember { mutableStateOf(true) }
    val isLoading by viewModel.isLoading
    val foundPerson by viewModel.foundPerson
    val errorMessage by viewModel.errorMessage
    val hasError by viewModel.hasError

    LaunchedEffect(Unit) {
        // Reset states when returning to the screen
        isMonitoring = true
        viewModel.clearError()
        viewModel.setOnAuthenticationSuccess {
            onAuthenticationSuccess(foundPerson)
        }
    }

    LaunchedEffect(Unit) {
        delay(1000)
        viewModel.talkToPerson()
    }

    LaunchedEffect(isMonitoring, hasError) {
        while (isMonitoring && !hasError) {
            withContext(Dispatchers.IO) {
                val humanAwareness = RoboterActions.getHumanAwarness()
                if (humanAwareness != null) {
                    withContext(Dispatchers.Main) {
                        viewModel.talkToPerson()
                        viewModel.takePicture()
                        isMonitoring = false
                    }
                }
            }
            delay(3000)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            isMonitoring = false
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Gesichtserkennung",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = {
                    // DEV-SHORTCUT: Gesichtserkennung überspringen
                    if (devModeSkipFaceRecognition) {
                        onAuthenticationSuccess("Nikola Mladenovic")
                        return@Button
                    }

                    if (errorMessage != null) {
                        viewModel.clearError()
                        isMonitoring = true
                        viewModel.takePicture()
                    }
                },
                modifier = Modifier.size(180.dp),
                shape = MaterialTheme.shapes.large,
                enabled = !isLoading && (errorMessage != null || devModeSkipFaceRecognition)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(60.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Kamera starten",
                        modifier = Modifier.size(100.dp)
                    )
                }
            }

            // Fehlermeldung anzeigen
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
