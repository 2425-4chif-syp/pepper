package com.example.mmg.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.mmg.viewmodel.MmgViewModel
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.mmg.R

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
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mitmachgeschichten",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    viewModel.emptyMmgList()
                    viewModel.loadMmgDtos()
                }
            ) {
                Text(text = "Geschichten laden")
            }
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { 
                    manuellSelected = true
                    showTimerDropdown = false
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (manuellSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(text = "Manuell")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { 
                    manuellSelected = false
                    showTimerDropdown = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!manuellSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(text = "Automatisch")
            }
            
            if (!manuellSelected && showTimerDropdown) {
                Spacer(modifier = Modifier.width(16.dp))
                
                Box {
                    var expanded by remember { mutableStateOf(false) }
                    
                    Button(
                        onClick = { expanded = true }
                    ) {
                        Text(text = "${selectedTimerSeconds}s")
                    }
                    
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf(2,5, 10, 15).forEach { seconds ->
                            DropdownMenuItem(
                                text = { 
                                    Text("${seconds} Sekunden") 
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

        Spacer(modifier = Modifier.height(16.dp))

        if (mmgList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(mmgList) { mmg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .height(80.dp)
                            .clickable {
                                navController.navigate("step")
                                viewModel.loadMmgSteps(
                                    id = mmg.id,
                                    isManual = manuellSelected,
                                    timerSeconds = selectedTimerSeconds
                                )
                                viewModel.resetStepCount()
                            },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            val storyIconBitmap = mmg.storyIcon?.id?.let { iconId ->
                                imageMap[iconId]
                            }

                            Box(
                                modifier = Modifier.size(80.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (storyIconBitmap != null) {
                                    Image(
                                        bitmap = storyIconBitmap,
                                        contentDescription = "Story Icon",
                                        modifier = Modifier.size(80.dp)
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.default_story_icon),
                                        contentDescription = "Default Story Icon",
                                        modifier = Modifier.size(80.dp)
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
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Icon(
                                imageVector = Icons.Default.PlayArrow, 
                                contentDescription = "Play"
                            )
                        }
                    }
                }
            }
        }
    }
}