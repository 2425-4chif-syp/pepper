package com.example.mmg.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import com.example.mmg.viewmodel.MmgViewModel
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.mmg.R
import com.example.mmg.ui.components.PepperCard
import com.example.mmg.ui.components.PepperSecondaryButton
import com.example.mmg.ui.components.PepperSegmentedControl
import com.example.mmg.ui.theme.PepperShapes

@Composable
fun MmgScreen(
    viewModel: MmgViewModel,
    navController: NavController
) {
    val mmgList by viewModel.mmgList.collectAsState()
    val imageMap by viewModel.imageMap.collectAsState()
    var manuellSelected by remember { mutableStateOf(true) }
    var selectedTimerSeconds by remember { mutableStateOf(2) }
    var showTimerDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (mmgList.isEmpty()) {
            viewModel.loadMmgDtos()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Kopfzeile: Titel links, die Nebenaktion rechts daneben - nicht daruntergesetzt,
        // damit die Liste sofort anfaengt.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Mitmachgeschichten",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                // Statuszeile: sagt, was da ist, statt es den Nutzer zaehlen zu lassen
                Text(
                    text = if (mmgList.isEmpty()) {
                        "Geschichten werden geladen"
                    } else {
                        "${mmgList.size} Geschichten - eine antippen zum Starten"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            PepperSecondaryButton(
                text = "Geschichten laden",
                onClick = {
                    viewModel.emptyMmgList()
                    viewModel.loadMmgDtos()
                }
            )
        }

        // Ablaufart: genau eines von beiden gilt, also eine segmentierte Auswahl und
        // keine zwei gleich aussehenden Schaltflaechen.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PepperSegmentedControl(
                options = listOf("Manuell", "Automatisch"),
                selectedIndex = if (manuellSelected) 0 else 1,
                onSelect = { index ->
                    manuellSelected = index == 0
                    showTimerDropdown = index == 1
                }
            )

            if (!manuellSelected && showTimerDropdown) {
                Spacer(modifier = Modifier.width(16.dp))

                Box {
                    var expanded by remember { mutableStateOf(false) }

                    // Steht direkt neben der Ablaufart, weil es nur dort etwas bedeutet
                    PepperSecondaryButton(
                        text = "${selectedTimerSeconds}s pro Schritt",
                        onClick = { expanded = true }
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        listOf(2, 5, 10, 15).forEach { seconds ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${seconds} Sekunden",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (seconds == selectedTimerSeconds) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                },
                                onClick = {
                                    selectedTimerSeconds = seconds
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        if (mmgList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    top = 20.dp,
                    bottom = 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mmgList) { mmg ->
                    PepperCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            navController.navigate("step")
                            viewModel.loadMmgSteps(
                                id = mmg.id,
                                isManual = manuellSelected,
                                timerSeconds = selectedTimerSeconds
                            )
                            viewModel.resetStepCount()
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val storyIconBitmap = mmg.storyIcon?.id?.let { iconId ->
                                imageMap[iconId]
                            }

                            // Das Bild sitzt in einer gerundeten Flaeche; ein leeres Feld
                            // haelt die Zeilenhoehe, damit die Liste beim Nachladen der
                            // Bilder nicht springt.
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(PepperShapes.control)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                if (storyIconBitmap != null) {
                                    Image(
                                        bitmap = storyIconBitmap,
                                        contentDescription = "Story Icon",
                                        modifier = Modifier.size(68.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.default_story_icon),
                                        contentDescription = "Default Story Icon",
                                        modifier = Modifier.size(68.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }

                            LaunchedEffect(mmg.storyIcon?.id) {
                                mmg.storyIcon?.id?.let { iconId ->
                                    if (!imageMap.containsKey(iconId)) {
                                        viewModel.loadImageFromApi(iconId)
                                    }
                                }
                            }

                            Text(
                                text = mmg.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 16.dp)
                            )

                            // Abspielzeichen im Akzent auf getoenter Flaeche: zeigt an,
                            // dass die ganze Zeile eine Schaltflaeche ist.
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
