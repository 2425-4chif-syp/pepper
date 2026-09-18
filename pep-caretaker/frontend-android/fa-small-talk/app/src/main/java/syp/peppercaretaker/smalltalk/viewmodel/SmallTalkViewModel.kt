package syp.peppercaretaker.smalltalk.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import syp.peppercaretaker.smalltalk.PepperFuncs
import syp.peppercaretaker.smalltalk.model.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SmallTalkViewModel(application: Application): AndroidViewModel(application) {

    var isSpeaking = mutableStateOf(false)
    var buttonPressed = mutableStateOf(false)

    // Config für Spracherkennung
    private val context = application.applicationContext
    private var speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

    private val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    }

    init {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {buttonPressed.value = true}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                buttonPressed.value = false
                Log.d("Spracherkennung", "Fehler: $error")
            }

            override fun onResults(results: Bundle?) {
                val heardText = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                Log.d("Gehörter Text","${heardText}")

                if(heardText.isNullOrEmpty()){
                    ActionOnError("Tut mir Leid ich konnte Sie nicht verstehen. Können Sie die Frage wiederholen?","")
                    buttonPressed.value = false
                    return;
                }

                viewModelScope.launch {
                    try {
                        val response = Api.sendPostRequestSmallTalk(heardText.toString())

                        if(response != "" && response != null){
                            withContext(Dispatchers.Main) {
                                buttonPressed.value = false
                                isSpeaking.value = true

                                val future = PepperFuncs.speak("${response}")

                                if (future == null) {
                                    Log.e("SmallTalkViewModel", "Failed to initiate speech")
                                    isSpeaking.value = false
                                }
                                else {
                                    future.thenConsume {
                                        isSpeaking.value = false
                                        Log.d("PepperSpeak", "Speech completed")
                                    }
                                }
                            }
                        }

                    } catch (e: Exception) {
                        ActionOnError("Die Internetverbindung ist schlecht","Fehler beim API-Aufruf: ${e.message}")
                        buttonPressed.value = false
                        isSpeaking. value = false
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

    fun ActionOnError(speakText:String, errorText: String ){
        val future = PepperFuncs.speak(speakText)

        if (future == null) {
            Log.e("Speak", "Qi-Context Fehler!")
        }
        else {
            future.thenConsume {
                Log.d("Fehler",errorText)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer.destroy()
    }
}