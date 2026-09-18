package com.pepper.mealplan.features.overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepper.mealplan.data.menu.MenuRepository
import com.pepper.mealplan.data.foods.FoodsRepository
import com.pepper.mealplan.network.dto.ApiMealPlanDto
import com.pepper.mealplan.network.dto.ApiFoodDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

// Eine Mahlzeit (z.B. Suppe, Hauptgericht 1, Dessert, Abendessen 1, ...)
data class MealItem(
    val title: String,
    val foodName: String,
    val foodType: String,   // "soup", "main", "dessert"
    val timeMinutes: Int,   // Uhrzeit als Minuten seit Mitternacht
    val pictureId: Int? = null  // ID des Fotos
)

// UI-Daten für einen Tag (Datum + alle Mahlzeiten)
data class DayMealsUi(
    val calendar: Calendar,
    val label: String,       // "Heute", "Morgen", "Übermorgen"
    val weekdayCode: String, // "MO", "DI", ...
    val menu: ApiMealPlanDto?,
    val meals: List<MealItem>
)

class MealPlanOverviewViewModel(
    private val foundPerson: String = ""
) : ViewModel() {

    var isLoading by mutableStateOf(true)
        private set

    // Heute + 2 Tage
    var threeDayMeals by mutableStateOf<List<DayMealsUi>>(emptyList())
        private set

    // Index der nächsten Mahlzeit für heute (0..5)
    var initialMealIndexToday by mutableStateOf(0)
        private set

    private val menuRepository = MenuRepository()
    private val foodsRepository = FoodsRepository()
    
    private var foodsMap by mutableStateOf<Map<Int, ApiFoodDto>>(emptyMap())

    init {
        refreshData()
    }

    fun getFoodById(id: Int?): ApiFoodDto? {
        return if (id != null) foodsMap[id] else null
    }

    fun refreshData() {
        viewModelScope.launch {
            isLoading = true

            // Lade alle Foods vom Backend
            val foodsResult = foodsRepository.getAllFoods()
            foodsResult.onSuccess { foods ->
                foodsMap = foods.filterNotNull().associateBy { it.id ?: 0 }
            }.onFailure { error ->
                println("Fehler beim Laden der Foods: ${error.message}")
            }

            val todayCal = Calendar.getInstance()

            // Heute + 2 Tage aufbauen
            val days = (0..2).map { offset ->
                val cal = (todayCal.clone() as Calendar).apply {
                    add(Calendar.DAY_OF_YEAR, offset)
                }
                val indexFromToday = offset
                val weekdayCode = cal.toWeekdayCode()
                val weekNumber = getWeekNumberForDate(cal)
                val weekDay = cal.toWeekDayIndex()

                // Lade Menü vom Backend
                var menu: ApiMealPlanDto? = null
                val menuResult = menuRepository.getMenuForDate(weekNumber, weekDay)
                menuResult.onSuccess { apiMenu ->
                    menu = apiMenu
                }.onFailure { error ->
                    println("Fehler beim Laden des Menüs für Tag $weekDay, Woche $weekNumber: ${error.message}")
                }

                buildDayMealsUi(
                    calendar = cal,
                    indexFromToday = indexFromToday,
                    weekdayCode = weekdayCode,
                    menu = menu
                )
            }

            threeDayMeals = days

            val nowMinutes = todayCal.get(Calendar.HOUR_OF_DAY) * 60 +
                    todayCal.get(Calendar.MINUTE)

            val todayUi = threeDayMeals.firstOrNull()
            initialMealIndexToday = calculateInitialMealIndex(
                nowMinutes = nowMinutes,
                day = todayUi
            )

            delay(300)
            isLoading = false
        }
    }


    // ---------- Hilfsfunktionen ----------

    private fun buildDayMealsUi(
        calendar: Calendar,
        indexFromToday: Int,
        weekdayCode: String,
        menu: ApiMealPlanDto?
    ): DayMealsUi {
        val label = when (indexFromToday) {
            0 -> "Heute"
            1 -> "Morgen"
            2 -> "Übermorgen"
            else -> getGermanWeekdayFromCode(weekdayCode)
        }

        if (menu == null) {
            return DayMealsUi(
                calendar = calendar,
                label = label,
                weekdayCode = weekdayCode,
                menu = null,
                meals = emptyList()
            )
        }

        fun foodName(food: ApiFoodDto?): String =
            food?.name ?: "Keine Angabe"

        // Debug: Prüfe ob Bilder vorhanden sind
        println("DEBUG: Soup picture ID: ${menu.soup?.picture?.id}, Food ID: ${menu.soup?.id}")
        println("DEBUG: Lunch1 picture ID: ${menu.lunch1?.picture?.id}, Food ID: ${menu.lunch1?.id}")
        println("DEBUG: LunchDessert picture ID: ${menu.lunchDessert?.picture?.id}, Food ID: ${menu.lunchDessert?.id}")
        println("DEBUG: Dinner1 picture ID: ${menu.dinner1?.picture?.id}, Food ID: ${menu.dinner1?.id}")

        // Nur die ersten Optionen für Overview (später personalisiert)
        // Verwende Food-ID statt Picture-ID, da Backend keine Picture-Daten sendet
        val meals = listOf(
            MealItem(
                title = "Suppe",
                foodName = foodName(menu.soup),
                foodType = "soup",
                timeMinutes = 11 * 60 + 30,
                pictureId = menu.soup?.id
            ),
            MealItem(
                title = "Hauptgericht",
                foodName = foodName(menu.lunch1),
                foodType = "main",
                timeMinutes = 12 * 60,
                pictureId = menu.lunch1?.id
            ),
            MealItem(
                title = "Dessert",
                foodName = foodName(menu.lunchDessert),
                foodType = "dessert",
                timeMinutes = 13 * 60,
                pictureId = menu.lunchDessert?.id
            ),
            MealItem(
                title = "Abendessen",
                foodName = foodName(menu.dinner1),
                foodType = "main",
                timeMinutes = 17 * 60 + 30,
                pictureId = menu.dinner1?.id
            )
        )

        return DayMealsUi(
            calendar = calendar,
            label = label,
            weekdayCode = weekdayCode,
            menu = menu,
            meals = meals
        )
    }

    private fun calculateInitialMealIndex(
        nowMinutes: Int,
        day: DayMealsUi?
    ): Int {
        val meals = day?.meals ?: return 0
        if (meals.isEmpty()) return 0

        val idx = meals.indexOfFirst { it.timeMinutes >= nowMinutes }
        return if (idx == -1) meals.lastIndex else idx
    }
}

// Tag → "MO", "DI", ...
private fun Calendar.toWeekdayCode(): String =
    when (get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "MO"
        Calendar.TUESDAY -> "DI"
        Calendar.WEDNESDAY -> "MI"
        Calendar.THURSDAY -> "DO"
        Calendar.FRIDAY -> "FR"
        Calendar.SATURDAY -> "SA"
        Calendar.SUNDAY -> "SO"
        else -> "MO"
    }

// Wochentag-Index (0 = Montag, 6 = Sonntag)
private fun Calendar.toWeekDayIndex(): Int =
    when (get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> 0
        Calendar.TUESDAY -> 1
        Calendar.WEDNESDAY -> 2
        Calendar.THURSDAY -> 3
        Calendar.FRIDAY -> 4
        Calendar.SATURDAY -> 5
        Calendar.SUNDAY -> 6
        else -> 0
    }

// 4-Wochen-Zyklus (WeekNumber 1..4) ohne java.time
private fun getWeekNumberForDate(calendar: Calendar): Int {
    val base = Calendar.getInstance().apply {
        set(Calendar.YEAR, 2025)
        set(Calendar.MONTH, Calendar.JANUARY)
        set(Calendar.DAY_OF_MONTH, 6)        // Montag
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

private fun getGermanWeekdayFromCode(code: String): String =
    when (code) {
        "MO" -> "Montag"
        "DI" -> "Dienstag"
        "MI" -> "Mittwoch"
        "DO" -> "Donnerstag"
        "FR" -> "Freitag"
        "SA" -> "Samstag"
        "SO" -> "Sonntag"
        else -> code
    }
