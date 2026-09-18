package com.example.menu

import MainMenuScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.menu.presentation.LoginScreen
import com.example.menu.ui.theme.MenuTheme
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.menu.common.Extras
import com.example.menu.viewmodel.LoginScreenViewModel

class MainActivity : ComponentActivity(), RobotLifecycleCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        QiSDK.register(this, this)


        setContent {
            MenuTheme {
                // Initialisiere NavController
                val navController = rememberNavController()

                // Setze den ViewModelStore
                navController.setViewModelStore(viewModelStore)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    // Setze den NavHost, nachdem ViewModelStore gesetzt wurde
                    NavHost(navController = navController, startDestination = "main_menu") {
                        //Das ist user Haputmenüü
                        composable("main_menu") {
                            MainMenuScreen(navController = navController)
                        }

                        //loginscreen mit Übergabe von Packganeame
                        composable(
                            route = "login_screen/{packageName}",
                            arguments = listOf(navArgument("packageName") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            val packageName =
                                backStackEntry.arguments?.getString("packageName") ?: ""
                            val vm: LoginScreenViewModel = viewModel()

                            LoginScreen(
                                onLoginClick = { id ->
                                    launchExternalApp(packageName, id)
                                },
                                onContinueWithoutLogin = {
                                    launchExternalApp(packageName, -1L) // als Long übergeben
                                },
                                navController = navController,
                                viewModel = vm
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onRobotFocusGained(qiContext: QiContext?) {
        // Context für Pepper um seine Funktionen aufrufen zu können
        RoboterActions.qiContext = qiContext
        Log.d("QiContext:", "Focus: ${RoboterActions.qiContext}")
        // robotExecute gibt an, ob die Roboter Funktionen beim Aufrufen ausgeführt werden sollen
        RoboterActions.robotExecute = true
    }

    override fun onRobotFocusLost() {
        QiSDK.unregister(this,this)
        super.onDestroy()
    }

    override fun onRobotFocusRefused(reason: String?) {

        // Im Logcat werden Fehlermeldungen ausgeben, falls die Verbindung unterbrochen wird
        if(reason != null){
            Log.d("Reason:",reason)
        }
    }

    private fun launchExternalApp(packageName: String, personId: Long) {
        if (personId < 0L) {
            Log.e("PepperMenu", "Abbruch: ungültige person_id=$personId")
            Toast.makeText(this, "Bitte zuerst eine Person auswählen", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            putExtra(Extras.PERSON_ID, personId)
        }
        if (intent != null) {
            Log.d("PepperMenu", "Launching pkg=$packageName, person_id=$personId")
            startActivity(intent)
        } else {
            Log.e("LoginScreen", "App mit Package $packageName nicht gefunden")
        }
    }


    /*private fun launchExternalApp(packageName: String, personId: Long) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            putExtra(Extras.PERSON_ID, personId)
        }
        if (intent != null) {
            Log.d("PepperMenu", "Launching pkg=$packageName, person_id=$personId")
            startActivity(intent)
        } else {
            Log.e("LoginScreen", "App mit Package $packageName nicht gefunden")
        }
    }*/
}