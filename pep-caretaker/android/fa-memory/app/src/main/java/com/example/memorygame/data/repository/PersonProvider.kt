package com.example.memorygame.data.repository

import com.example.memorygame.data.model.PersonIntent

interface PersonProvider {
    suspend fun getPerson(): PersonIntent?
}
