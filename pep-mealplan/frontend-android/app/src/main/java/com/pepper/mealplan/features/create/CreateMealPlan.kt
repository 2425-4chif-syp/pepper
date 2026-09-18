package com.pepper.mealplan.features.create

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CreateMealPlan(
    foundPerson: String = "",
    viewModel: CreateMealPlanViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CreateMealPlanViewModel(foundPerson) as T
            }
        }
    )
) {
    when {
        viewModel.showMealSelection && viewModel.selectedDay != null && viewModel.selectedWeek != null -> {
            // Meal selection view
            MealSelectionView(
                weekNumber = viewModel.selectedWeek!!,
                dayShort = viewModel.selectedDay!!,
                mealStep = viewModel.currentMealStep,
                onBackClick = { viewModel.backToDaySelection() },
                onMealSelected = { mealId -> viewModel.selectMeal(mealId) }
            )
        }
        viewModel.showDayView && viewModel.selectedWeek != null -> {
            // Day selection view
            DaySelectionView(
                weekNumber = viewModel.selectedWeek!!,
                completedDays = viewModel.completedDays,
                onBackClick = { viewModel.backToWeekSelection() },
                onDayClick = { dayShort -> viewModel.selectDay(dayShort) }
            )
        }
        else -> {
            // Week selection view
            WeekSelectionView(
                onWeekClick = { weekNumber -> viewModel.selectWeek(weekNumber) }
            )
        }
    }
}

@Composable
private fun WeekSelectionView(
    onWeekClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Mahlzeiten auswählen",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Week boxes in 2x2 grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WeekBox(
                weekNumber = 1,
                modifier = Modifier.weight(1f).height(150.dp),
                onClick = { onWeekClick(1) }
            )
            WeekBox(
                weekNumber = 2,
                modifier = Modifier.weight(1f).height(150.dp),
                onClick = { onWeekClick(2) }
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WeekBox(
                weekNumber = 3,
                modifier = Modifier.weight(1f).height(150.dp),
                onClick = { onWeekClick(3) }
            )
            WeekBox(
                weekNumber = 4,
                modifier = Modifier.weight(1f).height(150.dp),
                onClick = { onWeekClick(4) }
            )
        }
    }
}

@Composable
private fun WeekBox(
    weekNumber: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // Calculate date ranges for each week
    val dateRange = when (weekNumber) {
        1 -> "24.11.2025 - 30.11.2025"
        2 -> "01.12.2025 - 07.12.2025"
        3 -> "08.12.2025 - 14.12.2025"
        4 -> "15.12.2025 - 21.12.2025"
        else -> ""
    }
    
    Card(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Woche $weekNumber",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = dateRange,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}