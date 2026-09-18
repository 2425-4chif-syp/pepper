package pepper.bewegung

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.ui.platform.LocalContext
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import pepper.bewegung.RoboterActions
import pepper.bewegung.viewmodel.MyViewModel

class MainActivity : ComponentActivity(), RobotLifecycleCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Register the RobotLifecycleCallbacks to this Activity.
        QiSDK.register(this, this)
        setContent{
            Column() {
                Text("Hello Pepper")
                val animations = MyViewModel().getRawFileNames(LocalContext.current)  // Übergibt den Context der MainActivity

                Log.d("Files","${animations}")
            }
        }
    }
    override fun onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this Activity.
        QiSDK.unregister(this, this)
        super.onDestroy()
    }
    override fun onRobotFocusGained(qiContext: QiContext) {
        // The robot focus is gained.
        RoboterActions.setQiContext(qiContext = qiContext)

        //Für Emulator ausschalten!!
        RoboterActions.setRobotExecute(true)
    }

    override fun onRobotFocusLost() {
        // The robot focus is lost.
        QiSDK.unregister(this,this)

    }
    override fun onRobotFocusRefused(reason: String) {
        // The robot focus is refused.
        Log.d("Reason","${reason}")
    }
}