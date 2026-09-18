package com.example.menu.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.menu.RoboterActions
import com.example.menu.dto.Person
import com.example.menu.network.HttpInstance
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class LoginScreenViewModel(application: Application) : AndroidViewModel(application) {

    //Zustand für den ausgewählten Namen
    var selectedName = mutableStateOf("Anna Müller")
        private set

    // Zustand für den ausgewähltes Geschlecht
    var selectedGender = mutableStateOf("Frau")
        private set

    // ausgewählte Person von den Anmeldevorgang
    var selectedPerson : Person? = null

    //Personenlsite der Bewohner + Arbeiter
    var persons : List<Person>? = null

    // Namenliste für das LazyColumn im Screen
    var names = mutableStateOf<List<String>>(emptyList())
        private set

    var isLoading = mutableStateOf(false)

    // Zusatzsatz für API-Abfrage um nur den Namen zu bekommen
    val answerContext = "Bitte sag mir den Namen, welcher grad erwähnt wurde! Nur der Name bitte keine extra Wörter!"

    // Config für Spracherkennung
    private val context = application.applicationContext
    private var speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)


    private val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    }

    init {
        isLoading.value = true
        viewModelScope.launch {
            try {
                persons = HttpInstance.getPersons()
                names.value = persons?.map { p -> p.firstName + " " + p.lastName } ?: emptyList()

            } catch (e: Exception) {
                Log.e("Names", "Error loading names: ${e.message}")
                names.value = emptyList()
            } finally {
                isLoading.value = false
            }
        }


        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                Log.d("Spracherkennung", "Fehler: $error")
            }

            override fun onResults(results: Bundle?) {
                val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                Log.d("Sprache","${data}")

                isLoading.value = true

                viewModelScope.launch {
                    try {
                        val response = HttpInstance.sendPostRequestSmallTalk(data.toString() + answerContext)

                        if(response != "" && response != null){
                            val answer = response
                            withContext(Dispatchers.Main) {
                                findRightPerson(response = response)
                            }
                        }
                        else{
                            RoboterActions.speak("Tut mir Leid. Ich kann sie leider nicht erkennen.")
                        }
                    } catch (e: Exception) {
                        RoboterActions.speak("Tut mir Leid. Ich kann sie leider nicht erkennen.")
                        Log.e("API-Fehler", "Fehler beim API-Aufruf: ${e.message}")
                    }
                    finally {
                        isLoading.value = false
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startSpeechRecognition() {

        viewModelScope.launch(Dispatchers.Main) {
            speechRecognizer.startListening(speechRecognizerIntent)
        }
    }

    // richitge Person von der Antwort herausfinden
    fun findRightPerson(response: String){

        val parts = response.split(" ")
        val firstName = parts[0]
        val lastName = parts[1]

        val rightPerson : Person? = persons?.firstOrNull{ p -> p.firstName == firstName && p.lastName == lastName }

        if(rightPerson == null){
            RoboterActions.speak("Ich konnte keine richitge Person finden!")
        }
        else{
            selectedPerson = rightPerson
            setName(firstName + " " + lastName)
            setGender(rightPerson.gender)
            RoboterActions.speak("Sind Sie ${rightPerson.firstName + rightPerson.lastName}")
        }
    }

    // Name setzen für Screen
    fun setName(name: String) {
        selectedName.value = name
    }

    // Gender setzen für Screen
    fun setGender(gender: Boolean){

        if(gender == true){
            selectedGender.value = "Mann"
        }
        else{
            selectedGender.value = "Frau"
        }
    }

    fun captureAndRecognizePerson() {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val capturedImageDeferred = CompletableDeferred<ImageBitmap>()

            try {
                RoboterActions.speak("Ich mache kurz ein Foto von dir")

                // Warte auf das Bild
                RoboterActions.takePicture { image ->
                    capturedImageDeferred.complete(image)
                }

                // Hier wird gewartet, bis das Bild verfügbar ist
                val capturedImage = capturedImageDeferred.await()

                val response = HttpInstance.sendPostRequestImage(capturedImage)
                Log.d("Response", "${response}")

                withContext(Dispatchers.Main) {
                    if (isResponseValid(response) && response != "" && response != null) {
                        findRightPerson(response = response)
                    } else {
                        RoboterActions.speak("Tut mir Leid. Ich kann Sie leider nicht erkennen.")
                    }
                }
            } catch (e: Exception) {
                RoboterActions.speak("Tut mir Leid. Ich kann sie leider nicht erkennen.")
                Log.e("API-Fehler", "Fehler beim API-Aufruf: ${e.message}")
            }
            finally {
                isLoading.value = false
            }
        }
    }

    private fun isResponseValid(response: String): Boolean {
        val responseUpper = response.uppercase(Locale.getDefault())
        return responseUpper != "" && responseUpper != "NO MATCHING PERSON FOUND" && responseUpper != "ERROR PROCESSING IMAGE"
    }
}