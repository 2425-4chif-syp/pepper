package syp.peppercaretaker.smalltalk

import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.builder.SayBuilder

class PepperFuncs{
    companion object {

        var qiContext: QiContext? = null

        var onPepper: Boolean = true

        fun speak(text: String): Future<Void>? {
            if (onPepper) {
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
    }
}