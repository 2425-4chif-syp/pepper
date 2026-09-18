package com.pepper.mealplan.network.dto

import java.time.LocalDate

data class SpecialMealDto(
    val date: LocalDate,
    val soupId: Int? = null,
    val m1Id: Int? = null,
    val m2Id: Int? = null,
    val lunchDessertId: Int? = null,
    val a1Id: Int? = null,
    val a2Id: Int? = null
)