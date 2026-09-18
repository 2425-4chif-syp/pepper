package com.pepper.mealplan.network.dto

data class ApiMealPlanUpdateDto(
    val weekNumber: Int,
    val weekDay: Int,
    val soupId: Int? = null,
    val lunch1Id: Int? = null,
    val lunch2Id: Int? = null,
    val lunchDessertId: Int? = null,
    val dinner1Id: Int? = null,
    val dinner2Id: Int? = null
)
