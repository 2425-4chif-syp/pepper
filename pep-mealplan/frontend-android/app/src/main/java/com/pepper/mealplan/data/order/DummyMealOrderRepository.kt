package com.pepper.mealplan.data.order

import java.util.Calendar


 // Dummy-Implementation:
object DummyMealOrderRepository : MealOrderRepository {

     override fun getMissingMealsForNextDays(
         personName: String,
         days: Int
     ): List<MissingMealInfo> {
         if (personName.isBlank()) return emptyList()

         // =======================
         // TEST-MODUS: ALLES BESTELLT
         // =======================
         // return emptyList()

         // =======================
         // DUMMY-LOGIK (aktivieren, um fehlende Bestellungen anzeigen lassen)
         // =======================

         val today = Calendar.getInstance()
         val result = mutableListOf<MissingMealInfo>()

         for (offset in 0 until days) {
             val cal = (today.clone() as Calendar).apply {
                 add(Calendar.DAY_OF_YEAR, offset)
             }
             val dateKey = cal.toDateKey()

             // 🔴 DUMMY-LOGIK:
             // - Heute: nichts fehlt
             // - Morgen: Mittagessen (MAIN1) fehlt
             // - Übermorgen: Abendessen 1 (DINNER1) fehlt
             when (offset) {
                 1 -> result.add(
                     MissingMealInfo(
                         personName = personName,
                         dateKey = dateKey,
                         slot = MealSlot.MAIN1
                     )
                 )
                 2 -> result.add(
                     MissingMealInfo(
                         personName = personName,
                         dateKey = dateKey,
                         slot = MealSlot.DINNER1
                     )
                 )
             }
         }

         return result

     }
 }
