package com.example.mmg.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mmg.presentation.MmgScreen
import com.example.mmg.presentation.StepScreen
import com.example.mmg.viewmodel.MmgViewModel

@Composable
fun AppNavigaton(navController: NavHostController, mmgViewModel: MmgViewModel) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ){
        composable("home"){ MmgScreen(navController = navController, viewModel = mmgViewModel)}
        composable("step"){ StepScreen(navController = navController, viewModel = mmgViewModel)}
    }
}