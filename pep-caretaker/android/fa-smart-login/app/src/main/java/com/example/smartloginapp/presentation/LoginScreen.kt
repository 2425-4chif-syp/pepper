package com.example.smartloginapp.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartloginapp.ui.theme.SmartLoginAppTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoginScreen(onLoginClick: () -> Unit, onContinueWithoutLogin: () -> Unit) {
    var selectedName by remember { mutableStateOf("Hermine Mayer") }
    val names = listOf("Hermine Mayer", "Max Mustermann", "Anna Müller", "John Doe", "Max MusterMann", "Marc Laros")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 50.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        // Text "Sind sie Frau Hermine Mayer?"
        Text(
            text = "Sind sie Frau Hermine Mayer?",
            fontSize = 60.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            color = Color.Black,
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Zwei Buttons nebeneinander
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .weight(0.5f)
                    .height(100.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF4CAF50), // Grün
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Ja",
                    fontSize = 50.sp
                )
            }
            Button(
                onClick = onContinueWithoutLogin,
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF2196F3), // Blau
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Ohne Anmeldung weiter",
                    fontSize = 50.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        // Row für ScrollView und Icons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ScrollView für Namensauswahl
            Column(
                modifier = Modifier
                    .width(400.dp)
                    .height(400.dp)
                    .background(Color(0xFFE0E0E0)) // Hellgrau
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Text "Wählen Sie Ihren Namen aus"
                Text(
                    text = "Wählen Sie Ihren Namen aus",
                    fontSize = 24.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // LazyColumn für die Namensliste mit Scrollbar
                val scrollState = rememberLazyListState()
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(names.size) { index ->
                        Button(
                            onClick = { selectedName = names[index] },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (names[index] == selectedName) Color(0xFFFFEB3B) else Color.White, // Gelb für Auswahl
                                contentColor = Color.Black
                            )
                        ) {
                            Text(text = names[index], fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp)) // Abstand zwischen den Buttons
                    }
                }
            }

            // Column für Icons und Text
            Column(
                modifier = Modifier
                    .padding(start = 50.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Gesichtserkennung
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFFE0E0E0)) // Hellgrau
                        .padding(20.dp)
                        .fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { /* Handle Gesichtserkennung */ },
                        modifier = Modifier
                            .width(100.dp)
                            .height(100.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Gesichtserkennung",
                            modifier = Modifier.fillMaxSize(),
                            tint = Color(0xFFFFA500) // Orange
                        )
                    }
                    Text(
                        text = "Gesichtserkennung",
                        fontSize = 40.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(1.dp))

                // Spracherkennung
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFFE0E0E0)) // Hellgrau
                        .padding(20.dp)
                        .fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { /* Handle Spracherkennung */ },
                        modifier = Modifier
                            .width(100.dp)
                            .height(100.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Spracherkennung",
                            modifier = Modifier.fillMaxSize(),
                            tint = Color(0xFF4CAF50) // Grün
                        )
                    }
                    Text(
                        text = "Spracherkennung",
                        fontSize = 40.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    SmartLoginAppTheme {
        LoginScreen(
            onLoginClick = {},
            onContinueWithoutLogin = {}
        )
    }
}