package com.example.memorygame.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun InstructionsScreen(
    textToSpeech: TextToSpeech,
    navController: NavHostController
) {
    val instructionsText = """
        Willkommen im Memory-Spiel!
        
        Memory ist ein lustiges und spannendes Spiel, bei dem du dein Gedächtnis testen kannst. Dein Ziel ist es, immer zwei gleiche Bilder zu finden. Aber aufgepasst! Die Karten liegen verdeckt! Du kannst sie umdrehen, aber nur zwei auf einmal.
        
        Wenn die beiden Karten gleich sind, hast du ein Paar gefunden! Mach weiter und finde alle Paare. Aber wenn die Karten nicht übereinstimmen, werden sie wieder verdeckt. Merke dir, wo sie liegen, um später schneller zu sein.
        
        Das Besondere an diesem Memory-Spiel: Es spielt mit deinen eigenen Bildern! Vielleicht entdeckst du Bilder von dir, deiner Familie oder von besonderen Momenten, die dich zum Lächeln bringen. Dieses Memory-Spiel ist ein kleines Abenteuer durch die schönsten Erinnerungen deines Lebens.
        
        Kannst du alle Paare entdecken? Viel Spaß beim Spielen und genieße die Reise durch deine Erinnerungen!
    """.trimIndent()

    // Text-to-Speech für Pepper
    LaunchedEffect(Unit) {
        textToSpeech.speak(
            instructionsText,
            TextToSpeech.QUEUE_FLUSH,
            null,
            null
        )
    }

    // Inhalt des Screens
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        // Überschrift
        Text(
            text = "🌟 Willkommen im Memory-Spiel! 🌟",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            color = Color(0xFF00796B), // Grünlich-blauer Farbton
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Textabschnitte mit Emojis
        Text(
            text = "🎮 Memory ist ein lustiges und spannendes Spiel, bei dem du dein Gedächtnis testen kannst. Dein Ziel ist es, immer zwei gleiche Bilder zu finden. Aber aufgepasst! 👀 Die Karten liegen verdeckt! Du kannst sie umdrehen, aber nur zwei auf einmal.\n",
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "🃏 Wenn die beiden Karten gleich sind, hast du ein Paar gefunden! 🎉 Mach weiter und finde alle Paare. Aber wenn die Karten nicht übereinstimmen, werden sie wieder verdeckt. 🌀 Merke dir, wo sie liegen, um später schneller zu sein!\n",
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "🌈 Das Besondere an diesem Memory-Spiel: Es spielt mit deinen eigenen Bildern! 🌟 Vielleicht entdeckst du Bilder von dir, deiner Familie oder von besonderen Momenten, die dich zum Lächeln bringen. 😊 Dieses Memory-Spiel ist ein kleines Abenteuer durch die schönsten Erinnerungen deines Lebens. 💖\n",
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Das Spiel ist vorbei, wenn alle Paare gefunden sind. 🏆 Kannst du alle entdecken? 🧐 Viel Spaß beim Spielen und genieße die Reise durch deine Erinnerungen! ✨",
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }

    // Zurück-Button am unteren Bildschirmrand
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(
            onClick = {
                textToSpeech.stop()
                navController.navigate("main_menu")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            contentPadding = PaddingValues(16.dp),
        ) {
            Text(
                text = "Zurück zum Hauptmenü",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
