package com.pepper.mealplan.network.dto

data class MenuDto(
    val id: Int? = null,
    val weekNumber: Int,
    val weekday: String,
    val soupId: Int? = null,
    val m1Id: Int? = null,
    val m2Id: Int? = null,
    val lunchDessertId: Int? = null,
    val a1Id: Int? = null,
    val a2Id: Int? = null
)