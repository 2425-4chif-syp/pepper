package com.example.mmg.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mmg.R
import com.example.mmg.viewmodel.MmgViewModel
import com.example.mmg.ui.components.PepperPrimaryButton
import com.example.mmg.ui.components.PepperSecondaryButton
import com.example.mmg.ui.theme.PepperShapes

@Composable
fun StepScreen(
    viewModel: MmgViewModel,
    navController: NavController
) {

    val imageBitmap by viewModel.imageBitMap.collectAsState()
    val mmgSteps by viewModel.mmgStep.collectAsState()
    val stepsFinished by viewModel.stepsFinished.collectAsState()
    val isManualMode by viewModel.isManualMode.collectAsState()
    val buttonsEnabled by viewModel.buttonsEnabled.collectAsState()
    val stepCount by viewModel._stepCount.collectAsState()
    val isWaitingForFirstImage = stepCount == 1 && imageBitmap == null && mmgSteps.isNotEmpty()

    LaunchedEffect(Unit) {
        viewModel.setNavigationCallback {
            navController.popBackStack()
        }
    }

    LaunchedEffect(stepsFinished) {
        if (stepsFinished && !isManualMode) {
            kotlinx.coroutines.delay(3000L)
            navController.popBackStack()
        }
    }

    if(mmgSteps.isEmpty() || (stepCount == 0 && imageBitmap == null))
    {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center

        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
    else
    {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Bildbuehne: gerundet und mit feiner Linie abgesetzt, damit das Bild als
            // Flaeche auf der Seite liegt und nicht randlos im Nichts endet.
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 20.dp)
                    .clip(PepperShapes.sheet)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                        shape = PepperShapes.sheet
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isWaitingForFirstImage) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                } else if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap!!,
                        contentDescription = "Step Picture",
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.default_step_picture),
                        contentDescription = "Default Step Picture",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // Bedienleiste: eigene Flaeche mit Schatten, liegt sichtbar ueber dem Inhalt.
            // Ein durchscheinender Weichzeichner waere die Web-Entsprechung, den kann das
            // Tablet (Android 6) nicht - deshalb eine ruhige, deckende Flaeche.
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Fortschritt: beantwortet "wo bin ich, wie viel kommt noch".
                    // Am Ende steht der Balken voll, auch wenn der Zaehler im
                    // automatischen Ablauf schon zurueckgesetzt wurde.
                    val progress = when {
                        stepsFinished -> 1f
                        mmgSteps.isEmpty() -> 0f
                        else -> (stepCount.toFloat() / mmgSteps.size).coerceIn(0f, 1f)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (stepsFinished) {
                                "Geschichte zu Ende"
                            } else {
                                "Schritt ${stepCount.coerceAtLeast(1)} von ${mmgSteps.size}"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(PepperShapes.pill),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.secondaryContainer
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = if (isManualMode) {
                            Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
                        } else {
                            Arrangement.Center
                        }
                    ) {
                        PepperSecondaryButton(
                            text = "Abbrechen",
                            modifier = Modifier.width(180.dp),
                            enabled = buttonsEnabled,
                            onClick = {
                                viewModel.resetStepCount()
                                navController.popBackStack()
                            }
                        )

                        if (isManualMode) {
                            PepperPrimaryButton(
                                text = if (stepsFinished) "Fertig" else "Weiter",
                                modifier = Modifier.width(180.dp),
                                enabled = buttonsEnabled,
                                onClick = {
                                    if(stepsFinished){
                                        navController.popBackStack()
                                    }
                                    else{
                                        viewModel.displayStep()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

    }
}
