package com.pepper.mealplan.data.order

import java.util.Calendar

// Slot einer konkreten Mahlzeit im Tagesplan
enum class MealSlot(val index: Int) {
    SOUP(0),
    MAIN1(1),
    MAIN2(2),
    DESSERT(3),
    DINNER1(4),
    DINNER2(5)
}

// Wenn eine Bestellung fehlt
data class MissingMealInfo(
    val personName: String,
    val dateKey: String,  // Format: yyyy-MM-dd
    val slot: MealSlot
)

interface MealOrderRepository {
    /**
     * Liefert alle fehlenden Mahlzeiten für die nächsten [days] Tage
     * (inkl. heutigem Tag).
     */
    fun getMissingMealsForNextDays(
        personName: String,
        days: Int = 3
    ): List<MissingMealInfo>
}

/**
 * Zentraler Zugangspunkt – umschalten zwischen Dummy und echter Implementation
 */
object MealOrderRepositoryProvider {
    // Umschalten auf echte Backend-Implementation
    val repository: MealOrderRepository = RealMealOrderRepository
    // Für Tests: val repository: MealOrderRepository = DummyMealOrderRepository
}

fun Calendar.toDateKey(): String {
    val year = get(Calendar.YEAR)
    val month = get(Calendar.MONTH) + 1
    val day = get(Calendar.DAY_OF_MONTH)
    return String.format("%04d-%02d-%02d", year, month, day)
}
