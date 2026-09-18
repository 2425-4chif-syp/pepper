package com.example.memorygame.data.repository

import android.content.Intent
import com.example.memorygame.common.Extras
import com.example.memorygame.data.model.PersonIntent
import com.example.memorygame.data.remote.PersonApi

class IntentPersonProvider(private val intent: Intent, private val personApi: PersonApi) : PersonProvider {
    override suspend fun getPerson(): PersonIntent? {
        val personId = intent.getLongExtra(Extras.PERSON_ID, -1L)
        if (personId == -1L) return null

        return try {
            personApi.getPersonById(personId)
        } catch (e: Exception) {
            null
        }
    }
}

/*class IntentPersonProvider(private val intent: Intent) : PersonProvider {
    override fun getPerson(): PersonIntent {
        return PersonIntent(
            id = intent.getLongExtra("id", -1)
        )
    }
}*/
