package com.example.memorygame.data.remote

import com.example.memorygame.data.model.Person

data class PersonImageDto(
    val base64Image: String,
    val description: String,
    val id: Long,
    val person: Person
)
