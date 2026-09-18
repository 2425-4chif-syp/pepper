package com.pepper.mealplan.data.order

import com.pepper.mealplan.data.orders.OrdersRepository
import com.pepper.mealplan.data.residents.ResidentsRepository
import kotlinx.coroutines.runBlocking
import java.util.Calendar

// Real Implementation mit Backend-Anbindung
object RealMealOrderRepository : MealOrderRepository {

    private val ordersRepository = OrdersRepository()
    private val residentsRepository = ResidentsRepository()

    override fun getMissingMealsForNextDays(
        personName: String,
        days: Int
    ): List<MissingMealInfo> {
        if (personName.isBlank()) return emptyList()

        return runBlocking {
            try {
                // Hole Person-ID
                val residentsResult = residentsRepository.getResidents()
                var personId: Int? = null
                
                residentsResult.onSuccess { residents ->
                    val resident = residents.find { 
                        "${it.firstname} ${it.lastname}".equals(personName, ignoreCase = true)
                    }
                    personId = resident?.id
                }

                if (personId == null) {
                    println("Person '$personName' nicht gefunden")
                    return@runBlocking emptyList()
                }

                val result = mutableListOf<MissingMealInfo>()
                val today = Calendar.getInstance()

                // Prüfe die nächsten [days] Tage
                for (offset in 0 until days) {
                    val cal = (today.clone() as Calendar).apply {
                        add(Calendar.DAY_OF_YEAR, offset)
                    }
                    val dateKey = cal.toDateKey()

                    // Hole exportierte Bestellungen für dieses Datum
                    val ordersResult = ordersRepository.getExportedOrders(dateKey)
                    
                    ordersResult.onSuccess { orders ->
                        // Prüfe ob für diese Person an diesem Tag eine Bestellung existiert
                        // TODO: Hier müsstest du die Response-Struktur vom Backend kennen
                        // und entsprechend parsen, ob die Bestellung existiert
                        
                        // FALLBACK: Da wir die genaue Response-Struktur nicht kennen,
                        // nehmen wir an, dass keine Bestellungen vorhanden sind wenn die Liste leer ist
                        if (orders.isEmpty()) {
                            result.add(
                                MissingMealInfo(
                                    personName = personName,
                                    dateKey = dateKey,
                                    slot = MealSlot.MAIN1
                                )
                            )
                        }
                    }.onFailure { error ->
                        println("Fehler beim Laden der Bestellungen für $dateKey: ${error.message}")
                    }
                }

                result
            } catch (e: Exception) {
                println("Fehler in getMissingMealsForNextDays: ${e.message}")
                emptyList()
            }
        }
    }
}
