package com.example.mmg

import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.actuation.Animate
import com.aldebaran.qi.sdk.`object`.actuation.Animation
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.builder.AnimateBuilder
import com.aldebaran.qi.sdk.builder.AnimationBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import java.util.concurrent.Future

class RoboterActions {

    companion object {

        var qiContext: QiContext? = null
        var robotExecute: Boolean = false

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

        fun animation(ressource: Int){
            if(robotExecute){
                var animation: Future<Animation>?
                var animate: Future<Animate>?
                animation = AnimationBuilder.with(qiContext).withResources(ressource).buildAsync()
                animate = AnimateBuilder.with(qiContext).withAnimation(animation!!.get()).buildAsync()
                animate!!.get().async().run()
            }
        }
    }
}