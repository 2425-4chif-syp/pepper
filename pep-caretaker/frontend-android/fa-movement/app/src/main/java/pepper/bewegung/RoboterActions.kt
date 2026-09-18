package pepper.bewegung

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.actuation.Animation
import com.aldebaran.qi.sdk.`object`.camera.TakePicture
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.`object`.image.EncodedImage
import com.aldebaran.qi.sdk.`object`.image.EncodedImageHandle
import com.aldebaran.qi.sdk.builder.AnimationBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.builder.TakePictureBuilder
import pepper.bewegung.viewmodel.MyViewModel
import java.nio.ByteBuffer
import java.util.concurrent.Future


class RoboterActions {

    companion object {

        // Kontext wird benötigt um die Verbindung mit dem Roboter zu gewährleisten
        private var qiContext: QiContext? = null
        private var robotExecute: Boolean = false

        fun setQiContext(qiContext: QiContext){
            this.qiContext = qiContext
        }

        fun setRobotExecute(robotExecute: Boolean){
            this.robotExecute = robotExecute
        }


        fun speak(text: String): Future<Void>? {
            if (robotExecute) {
                val say: Future<Say>? = SayBuilder.with(qiContext)
                    .withText(text)
                    .buildAsync()

                while (!say!!.isDone) {
                }

                return try {
                    say.get().async().run()
                } catch (e: Exception) {
                    null
                }
            }
            return null;
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

    }
}