package com.pepper.mealplan.features.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DaySelectionView(
    weekNumber: Int,
    completedDays: Set<String>,
    onBackClick: () -> Unit,
    onDayClick: (String) -> Unit = {}
) {
    val weekdays = listOf("MO", "DI", "MI", "DO", "FR", "SA", "SO")
    val weekdayNames = listOf("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag")
    
    // Calculate dates for the selected week
    val dates = getDatesForWeek(weekNumber)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Zurück"
                )
            }
            
            Text(
                text = "Woche $weekNumber - Tage auswählen",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Days in a horizontal scrollable row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(weekdays.zip(weekdayNames).zip(dates)) { (dayInfo, date) ->
                val (dayShort, dayName) = dayInfo
                val isCompleted = completedDays.contains(dayShort)
                DayBox(
                    dayShort = dayShort,
                    dayName = dayName,
                    date = date,
                    isCompleted = isCompleted,
                    onClick = { onDayClick(dayShort) }
                )
            }
        }
    }
}

@Composable
private fun DayBox(
    dayShort: String,
    dayName: String,
    date: String,
    isCompleted: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(150.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = dayShort,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (isCompleted) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isCompleted) 
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (isCompleted) {
                    Text(
                        text = "✓ Fertig",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun getDatesForWeek(weekNumber: Int): List<String> {
    return when (weekNumber) {
        1 -> listOf("25.11", "26.11", "27.11", "28.11", "29.11", "30.11", "01.12")
        2 -> listOf("02.12", "03.12", "04.12", "05.12", "06.12", "07.12", "08.12")
        3 -> listOf("09.12", "10.12", "11.12", "12.12", "13.12", "14.12", "15.12")
        4 -> listOf("16.12", "17.12", "18.12", "19.12", "20.12", "21.12", "22.12")
        else -> emptyList()
    }
}