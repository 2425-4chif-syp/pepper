package com.example.menu.common

import android.net.Uri
import androidx.navigation.NavHostController

fun NavHostController.openLoginFor(packageName: String) {
    navigate("login_screen/${Uri.encode(packageName)}") // wichtig: encoden!
}
