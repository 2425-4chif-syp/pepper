package com.pepper.mealplan

import android.content.ContentValues
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.actuation.Animate
import com.aldebaran.qi.sdk.`object`.actuation.Animation
import com.aldebaran.qi.sdk.`object`.camera.TakePicture
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.`object`.human.Human
import com.aldebaran.qi.sdk.`object`.humanawareness.HumanAwareness
import com.aldebaran.qi.sdk.`object`.image.EncodedImage
import com.aldebaran.qi.sdk.`object`.image.EncodedImageHandle
import com.aldebaran.qi.sdk.builder.AnimateBuilder
import com.aldebaran.qi.sdk.builder.AnimationBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.builder.TakePictureBuilder
import java.nio.ByteBuffer
import java.util.concurrent.Future

class RoboterActions {

    companion object {

        var qiContext: QiContext? = null
        var robotExecute: Boolean = false

        private var lastSpeech: Future<Void>? = null

        fun speak(text: String): java.util.concurrent.Future<Void>? {
            if (robotExecute) {
                val say: java.util.concurrent.Future<Say>? = SayBuilder.with(qiContext)
                    .withText(text)
                    .buildAsync()

                while (!say!!.isDone) {
                }

                return try {
                    val runFuture = say.get().async().run()
                    lastSpeech = runFuture
                    runFuture
                } catch (e: Exception) {
                    null
                }
            }
            return null
        }


        fun stopSpeaking() {
            if (robotExecute) {
                try {
                    lastSpeech?.cancel(true)
                } catch (e: Exception) {
                    Log.e(ContentValues.TAG, "Error stopping speech", e)
                } finally {
                    lastSpeech = null
                }
            }
        }


        fun animation(ressource: Int){
            if(robotExecute){
                var animation: Future<Animation>?
                var animate: Future<Animate>?
                animation = AnimationBuilder.with(qiContext).withResources(ressource).buildAsync()
                animate = AnimateBuilder.with(qiContext).withAnimation(animation!!.get()).buildAsync()
                animate!!.get().async().run()
            }
        }

        fun takePicture(onImageCaptured: (ImageBitmap) -> Unit){
            if (robotExecute) {
                val takePictureFuture: com.aldebaran.qi.Future<TakePicture>? = TakePictureBuilder.with(qiContext).buildAsync()

                val timestampedImageHandleFuture = takePictureFuture?.andThenCompose { takePicture ->
                    Log.i(ContentValues.TAG, "Take picture launched!")
                    takePicture.async().run()
                }
                timestampedImageHandleFuture?.andThenConsume { timestampedImageHandle ->
                    Log.i(ContentValues.TAG, "Picture taken")
                    val encodedImageHandle: EncodedImageHandle = timestampedImageHandle.image
                    val encodedImage: EncodedImage = encodedImageHandle.value

                    val buffer: ByteBuffer = encodedImage.data
                    buffer.rewind()
                    val pictureArray = ByteArray(buffer.remaining())
                    buffer.get(pictureArray)

                    val pictureBitmap = BitmapFactory.decodeByteArray(pictureArray, 0, pictureArray.size)
                    val pictureImageBitmap = pictureBitmap.asImageBitmap()
                    Log.i(ContentValues.TAG, "Picture: ${pictureImageBitmap}")

                    onImageCaptured(pictureImageBitmap)
                }
            }
        }

        fun getHumanAwarness(): Human?{
            if(robotExecute){
                val humanAwareness: HumanAwareness = qiContext!!.humanAwareness
                return humanAwareness.engagedHuman
            }
            return null
        }
    }
}