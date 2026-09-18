package com.pepper.mealplan.network.dto

import java.time.LocalDate
import java.time.LocalDateTime

data class OrderDto(
    val id: Int? = null,
    val date: LocalDate,
    val userId: Int,
    val menuId: Int,
    val dessertSelected: Int? = null,
    val selectedLunchId: Int? = null,
    val selectedDinnerId: Int? = null,
    val orderedAt: LocalDateTime? = null
)