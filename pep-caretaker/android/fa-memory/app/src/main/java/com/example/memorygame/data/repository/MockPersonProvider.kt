package com.example.memorygame.data.repository

import com.example.memorygame.data.model.Person
import com.example.memorygame.data.model.PersonIntent
import com.example.memorygame.data.remote.PersonApi

class MockPersonProvider(
    private val personId: Long,
    private val personApi: PersonApi
) : PersonProvider {

    override suspend fun getPerson(): PersonIntent {
        val person = personApi.getPersonById(personId)

        return PersonIntent(
            id = person.id,
            firstName = person.firstName,
            lastName = person.lastName
        )
    }
}
