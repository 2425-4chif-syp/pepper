package com.pepper.mealplan.features.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pepper.mealplan.RoboterActions
import com.pepper.mealplan.data.order.MealOrderRepositoryProvider
import com.pepper.mealplan.data.order.MealSlot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

@Composable
fun OrderReminderScreen(
    foundPerson: String,
    onGoToOrder: () -> Unit,
    onShowMenu: () -> Unit
) {
    val repository = MealOrderRepositoryProvider.repository
    val missingMeals = remember(foundPerson) {
        repository.getMissingMealsForNextDays(foundPerson, days = 3)
    }

    val namePart = if (foundPerson.isNotBlank()) "$foundPerson, " else ""

    // ---------- Text für Anzeige ----------
    val mainText = remember(missingMeals) {
        if (missingMeals.isEmpty()) {
            namePart + "alle Mahlzeiten der nächsten Tage sind bereits bestellt."
        } else {
            if (missingMeals.size == 1) {
                val first = missingMeals.first()
                val slotText = mealSlotText(first.slot)

                val weekDay = formatWeekday(first.dateKey)      // z.B. "Donnerstag"
                val niceDate = formatDateKey(first.dateKey)     // z.B. "18.12.2025"

                namePart +
                        "für $weekDay, den $niceDate hast du $slotText noch nicht bestellt.\n\n" +
                        "Wenn du mit der Bestellung fortfahren möchtest, drücke bitte auf den blauen Button.\n" +
                        "Wenn du nur den Essensplan ansehen möchtest, drücke auf den grünen Button."
            } else {
                val header =
                    namePart + "für die nächsten Tage hast du noch nicht alle Mahlzeiten bestellt:\n\n"

                val listText = missingMeals.joinToString(separator = "\n") { info ->
                    val weekDay = formatWeekday(info.dateKey)
                    val niceDate = formatDateKey(info.dateKey)
                    val slotText = mealSlotText(info.slot)
                    "- am $weekDay, den $niceDate: $slotText"
                }

                header +
                        listText +
                        "\n\nWenn du mit der Bestellung fortfahren möchtest, drücke bitte auf den blauen Button.\n" +
                        "Wenn du nur den Essensplan ansehen möchtest, drücke auf den grünen Button."
            }
        }
    }

    // ---------- Text für Pepper ----------
    val speechText = remember(missingMeals) {
        if (missingMeals.isEmpty()) {
            namePart + "alle Mahlzeiten der nächsten drei Tage sind bereits bestellt."
        } else {
            if (missingMeals.size == 1) {
                val first = missingMeals.first()
                val slotText = mealSlotText(first.slot)

                val weekDay = formatWeekday(first.dateKey)
                val niceDate = formatDateKey(first.dateKey)

                namePart +
                        "für $weekDay, hast du $slotText noch nicht bestellt. " +
                        "Drücke auf den blauen Button, um mit der Bestellung fortzufahren. " +
                        "Wenn du nur den Essensplan ansehen möchtest, drücke auf den grünen Button."
            } else {
                val header =
                    namePart + "für die nächsten Tage hast du noch nicht alle Mahlzeiten bestellt: "

                val listText = missingMeals.joinToString(separator = " ") { info ->
                    val weekDay = formatWeekday(info.dateKey)
                    val niceDate = formatDateKey(info.dateKey)
                    val slotText = mealSlotText(info.slot)
                    "am $weekDay, $slotText."
                }

                header + listText +
                        " Drücke auf den blauen Button, um mit der Bestellung fortzufahren. " +
                        "Wenn du nur den Essensplan ansehen möchtest, drücke auf den grünen Button."
            }
        }
    }

    // ---------- Pepper sprechen (alte Sprache stoppen, dann neuen Text) ----------
    LaunchedEffect(speechText) {
        if (speechText.isNotBlank()) {
            withContext(Dispatchers.IO) {
                RoboterActions.stopSpeaking()
                RoboterActions.speak(speechText)
            }
        }
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        // Oben: scrollbarer Text
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(bottom = 120.dp),
        ) {
            Text(
                text = "Bestell-Erinnerung",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Text(
                text = mainText,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Unten: zwei Buttons nebeneinander
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    RoboterActions.stopSpeaking()
                    onGoToOrder()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary
                )
            ) {
                Text(
                    text = "Bestellung starten",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = {
                    RoboterActions.stopSpeaking()
                    onShowMenu()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF4CAF50)
                )
            ) {
                Text(
                    text = "Nur Essensplan anzeigen",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun mealSlotText(slot: MealSlot): String =
    when (slot) {
        MealSlot.SOUP -> "die Suppe"
        MealSlot.MAIN1, MealSlot.MAIN2 -> "das Mittagessen"
        MealSlot.DESSERT -> "das Dessert"
        MealSlot.DINNER1, MealSlot.DINNER2 -> "das Abendessen"
    }

private fun formatWeekday(dateKey: String): String {
    return try {
        val parts = dateKey.split("-")
        if (parts.size == 3) {
            val year = parts[0].toInt()
            val month = parts[1].toInt() - 1 // Calendar: 0-basiert
            val day = parts[2].toInt()

            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
            }

            cal.getDisplayName(
                Calendar.DAY_OF_WEEK,
                Calendar.LONG,
                Locale.GERMAN
            ) ?: ""
        } else {
            ""
        }
    } catch (e: Exception) {
        ""
    }
}

private fun formatDateKey(dateKey: String): String {
    // yyyy-MM-dd -> dd.MM.yyyy
    return try {
        val parts = dateKey.split("-")
        if (parts.size == 3) {
            "${parts[2]}.${parts[1]}.${parts[0]}"
        } else {
            dateKey
        }
    } catch (e: Exception) {
        dateKey
    }
}
