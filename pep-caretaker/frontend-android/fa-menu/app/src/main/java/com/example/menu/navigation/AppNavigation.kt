package com.example.menu.navigation

import MainMenuScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.menu.screens.*

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "main_menu"
    ) {
        composable("main_menu") {
            MainMenuScreen(navController = navController)
        }
        composable("mitmachgeschichte_screen") {
            MitmachgeschichteScreen(navController = navController)
        }
        composable("memory_screen") {
            MemoryScreen(navController = navController)
        }
        composable("tic_tac_toe_screen") {
            TicTacToeScreen(navController = navController)
        }
        composable("fang_den_dieb_screen") {
            FangDenDiebScreen(navController = navController)
        }
        composable("essensplan_screen") {
            EssensplanScreen(navController = navController)
        }
    }
}