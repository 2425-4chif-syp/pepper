package com.pepper.mealplan.features.create

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepper.mealplan.data.orders.OrdersRepository
import com.pepper.mealplan.data.residents.ResidentsRepository
import kotlinx.coroutines.launch
import java.util.Calendar

// Meal selection steps
enum class MealStep {
    SOUP, MAIN, DESSERT, EVENING
}

class CreateMealPlanViewModel(
    private val foundPerson: String = ""
) : ViewModel() {
    
    var selectedWeek by mutableStateOf<Int?>(null)
        private set
    
    var showDayView by mutableStateOf(false)
        private set
    
    var selectedDay by mutableStateOf<String?>(null)
        private set
    
    var showMealSelection by mutableStateOf(false)
        private set
    
    var currentMealStep by mutableStateOf(MealStep.SOUP)
        private set
    
    var completedDays by mutableStateOf<Set<String>>(emptySet())
        private set
    
    // Selected meal IDs for current day
    var selectedSoupId by mutableStateOf<Int?>(null)
        private set
    var selectedMainId by mutableStateOf<Int?>(null)
        private set
    var selectedDessertId by mutableStateOf<Int?>(null)
        private set
    var selectedEveningId by mutableStateOf<Int?>(null)
        private set

    var isSubmitting by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    val personName: String get() = foundPerson

    private val ordersRepository = OrdersRepository()
    private val residentsRepository = ResidentsRepository()
    private var personId: Int? = null

    init {
        loadPersonId()
    }

    private fun loadPersonId() {
        viewModelScope.launch {
            val result = residentsRepository.getResidents()
            result.onSuccess { residents ->
                // Finde Person anhand des Namens
                val resident = residents.find { 
                    "${it.firstname} ${it.lastname}".equals(foundPerson, ignoreCase = true)
                }
                personId = resident?.id
                if (personId == null) {
                    println("Warnung: Person '$foundPerson' wurde nicht in der Datenbank gefunden")
                }
            }.onFailure { error ->
                println("Fehler beim Laden der Bewohner: ${error.message}")
            }
        }
    }
    
    fun selectWeek(weekNumber: Int) {
        selectedWeek = weekNumber
        showDayView = true
    }
    
    fun backToWeekSelection() {
        selectedWeek = null
        showDayView = false
        resetMealSelection()
    }
    
    fun selectDay(dayShort: String) {
        selectedDay = dayShort
        showMealSelection = true
        currentMealStep = MealStep.SOUP
    }
    
    fun backToDaySelection() {
        showMealSelection = false
        selectedDay = null
        resetMealSelection()
    }
    
    fun selectMeal(mealId: Int) {
        when (currentMealStep) {
            MealStep.SOUP -> {
                selectedSoupId = mealId
                currentMealStep = MealStep.MAIN
            }
            MealStep.MAIN -> {
                selectedMainId = mealId
                currentMealStep = MealStep.DESSERT
            }
            MealStep.DESSERT -> {
                selectedDessertId = mealId
                currentMealStep = MealStep.EVENING
            }
            MealStep.EVENING -> {
                selectedEveningId = mealId
                submitOrder()
            }
        }
    }
    
    private fun submitOrder() {
        viewModelScope.launch {
            if (personId == null) {
                errorMessage = "Person nicht gefunden"
                resetMealSelection()
                return@launch
            }

            val lunchId = selectedMainId ?: return@launch
            val dinnerId = selectedEveningId ?: return@launch
            val dateStr = calculateDateForDay(selectedDay ?: return@launch)

            isSubmitting = true
            errorMessage = null

            val result = ordersRepository.upsertOrder(
                date = dateStr,
                personId = personId!!,
                selectedLunchId = lunchId,
                selectedDinnerId = dinnerId
            )

            result.onSuccess {
                println("Bestellung erfolgreich gespeichert für $dateStr")
                completeMealSelection()
            }.onFailure { error ->
                errorMessage = "Fehler beim Speichern: ${error.message}"
                println("Fehler beim Speichern der Bestellung: ${error.message}")
            }

            isSubmitting = false
        }
    }

    private fun calculateDateForDay(dayShort: String): String {
        val weekNumber = selectedWeek ?: 1
        val today = Calendar.getInstance()
        val todayWeek = getWeekNumberForDate(today)
        
        // Berechne die Differenz in Wochen
        val weekDiff = weekNumber - todayWeek
        
        // Finde den richtigen Tag in der Woche
        val dayIndex = when (dayShort) {
            "MO" -> Calendar.MONDAY
            "DI" -> Calendar.TUESDAY
            "MI" -> Calendar.WEDNESDAY
            "DO" -> Calendar.THURSDAY
            "FR" -> Calendar.FRIDAY
            "SA" -> Calendar.SATURDAY
            "SO" -> Calendar.SUNDAY
            else -> Calendar.MONDAY
        }

        val targetDate = Calendar.getInstance().apply {
            add(Calendar.WEEK_OF_YEAR, weekDiff)
            set(Calendar.DAY_OF_WEEK, dayIndex)
        }

        return String.format(
            "%04d-%02d-%02d",
            targetDate.get(Calendar.YEAR),
            targetDate.get(Calendar.MONTH) + 1,
            targetDate.get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun getWeekNumberForDate(calendar: Calendar): Int {
        val base = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2025)
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_MONTH, 6)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val diffMillis = calendar.timeInMillis - base.timeInMillis
        val millisPerWeek = 7L * 24L * 60L * 60L * 1000L
        val weeksBetween = diffMillis / millisPerWeek

        var index = (weeksBetween % 4).toInt()
        if (index < 0) index += 4
        return index + 1 // 1..4
    }
    
    private fun completeMealSelection() {
        selectedDay?.let { day ->
            completedDays = completedDays + day
        }
        backToDaySelection()
    }
    
    private fun resetMealSelection() {
        selectedSoupId = null
        selectedMainId = null
        selectedDessertId = null
        selectedEveningId = null
        currentMealStep = MealStep.SOUP
    }
}