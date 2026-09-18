package com.example.mmg.viewmodel

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mmg.RoboterActions
import com.example.mmg.dto.EmoteDto
import com.example.mmg.dto.MmgDto
import com.example.mmg.dto.StepDto
import com.example.mmg.dto.getEmotes
import com.example.mmg.network.HttpInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.util.*

class MmgViewModel() : ViewModel() {

    private val _mmgList = MutableStateFlow<List<MmgDto>>(emptyList())
    val mmgList = _mmgList.asStateFlow()
    private val _mmgSteps = MutableStateFlow<List<StepDto>>(emptyList())
    val mmgStep = _mmgSteps.asStateFlow()
    val imageBitMap = MutableStateFlow<ImageBitmap?>(null)
    private val _imageMap = MutableStateFlow<Map<Int, ImageBitmap>>(emptyMap())
    val imageMap = _imageMap.asStateFlow()
    val _stepCount = MutableStateFlow(0)
    val stepsFinished = MutableStateFlow(false)
    var emotes: List<EmoteDto> = emptyList()
    val isManualMode = MutableStateFlow(true)
    val buttonsEnabled = MutableStateFlow(true)
    private var navigationCallback: (() -> Unit)? = null

    fun setNavigationCallback(callback: () -> Unit) {
        navigationCallback = callback
    }

    fun incrementStepCount() {
        _stepCount.value += 1
    }

    fun resetStepCount(){
        _stepCount.value = 0
    }

    private suspend fun displayStepsAutomatically(timerSeconds: Int) {
        for ((index, step) in _mmgSteps.value.withIndex()) {
            if(step.image!!.id != 0){
                loadImageFromApi(step.image.id)
            }

            RoboterActions.speak(step.text)

            val emote = getEmote(stepDto = step)
            if(emote != -1){
                RoboterActions.animation(emote)
            }

            incrementStepCount()
            kotlinx.coroutines.delay(timerSeconds * 1000L + step.durationInSeconds * 1000L)
        }
        
        stepsFinished.value = true
        resetStepCount()
        RoboterActions.speak("Die Geschichte ist zu Ende!")
        kotlinx.coroutines.delay(2000L)
        navigationCallback?.invoke()
    }

    fun displayStep(){

        if(_stepCount.value >= _mmgSteps.value.size){
            if(!stepsFinished.value) {
                stepsFinished.value = true
                RoboterActions.speak("Die Geschichte ist zu Ende!")
                resetStepCount()
            }
            return
        }

        val stepDto: StepDto = _mmgSteps.value[_stepCount.value]

        buttonsEnabled.value = false
        
        if(stepDto.image!!.id != 0){
            viewModelScope.launch {
                loadImageFromApi(stepDto.image.id)
            }
        }

        RoboterActions.speak(stepDto.text)

        val emote = getEmote(stepDto = stepDto)

        if(emote != -1){
            RoboterActions.animation(emote)
        }

        viewModelScope.launch {
            kotlinx.coroutines.delay((stepDto.durationInSeconds + 1) * 1000L)
            buttonsEnabled.value = true
        }

        incrementStepCount()

        if(_stepCount.value >= _mmgSteps.value.size){
            stepsFinished.value = true
            RoboterActions.speak("Die Geschichte ist zu Ende!")
        }
    }

    suspend fun loadImageFromApi(imageId: Int) {
        try {
            val imageBytes = HttpInstance.fetchImage(imageId)
            if (imageBytes != null) {
                val bitmap = byteArrayToBitmap(imageBytes)
                imageBitMap.value = bitmap
                _imageMap.value = _imageMap.value.toMutableMap().apply { put(imageId, bitmap!!) }
            } else {
                Log.e("MmgViewModel", "Failed to load image with id: $imageId")
                imageBitMap.value = null
            }
        } catch (e: Exception) {
            Log.e("MmgViewModel", "Error loading image with id: $imageId", e)
            imageBitMap.value = null
        }
    }

    fun getEmote(stepDto: StepDto): Int{

        var emoteName = ""

        if(stepDto.durationInSeconds == 0){
            emoteName = stepDto.move!!.name.lowercase(Locale.GERMAN) + "_" + "5"
        }
        else{
            emoteName = stepDto.move!!.name.lowercase(Locale.GERMAN) + "_" + stepDto.durationInSeconds.toString()
        }

        val rightEmote : EmoteDto? = emotes.filter{
                e -> e.name == emoteName
        }.firstOrNull()

        if(rightEmote != null){
            return rightEmote.path;
        }
        return -1;
    }

    fun loadMmgDtos() {
        emotes = getEmotes()
        viewModelScope.launch {
            val result = HttpInstance.fetchMmgDtos()

            if (result != null) {
                _mmgList.value = result
                Log.d("Mmgs","${_mmgList.value}")
                val iconLoadingJobs = result.mapNotNull { mmgDto ->
                    mmgDto.storyIcon?.id?.let { iconId ->
                        if (!_imageMap.value.containsKey(iconId)) {
                            viewModelScope.launch {
                                loadImageFromApi(iconId)
                            }
                        } else null
                    }
                }
            }
            else{
                RoboterActions.speak("Ich habe keine Mitmachgeschichten gefunden")
            }
        }
    }

    fun emptyMmgList(){
        _mmgList.value = emptyList()
    }

    fun loadMmgSteps(id: Int, isManual: Boolean, timerSeconds: Int){
        resetValues()
        isManualMode.value = isManual
        viewModelScope.launch {
            val result = HttpInstance.fetchMmgSteps(id)
            Log.d("Steps","${result}")

            if(result != null){
                _mmgSteps.value = result

                if(_mmgSteps.value != null){
                    if(isManual){
                        displayStep()
                    }
                    else{
                        displayStepsAutomatically(timerSeconds = timerSeconds)
                    }
                }
            }
            else{
                RoboterActions.speak("Ich habe keine Informationen gefunden!")
            }
        }
    }

    fun resetValues(){
        imageBitMap.value = null
        _mmgSteps.value = emptyList()
        stepsFinished.value = false
    }

    fun byteArrayToBitmap(byteArray: ByteArray): ImageBitmap? {
        return try {
            val options = BitmapFactory.Options()

            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(ByteArrayInputStream(byteArray), null, options)

            options.inSampleSize = calculateInSampleSize(options, 800, 600)

            options.inJustDecodeBounds = false
            options.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565
            
            val bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(byteArray), null, options)
            bitmap?.asImageBitmap()
        } catch (e: OutOfMemoryError) {
            Log.e("MmgViewModel", "OutOfMemoryError when decoding byte array image", e)
            null
        } catch (e: Exception) {
            Log.e("MmgViewModel", "Error decoding byte array image", e)
            null
        }
    }
    
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}