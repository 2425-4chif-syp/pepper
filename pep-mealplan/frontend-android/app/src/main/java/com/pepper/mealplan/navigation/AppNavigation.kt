package com.pepper.mealplan.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pepper.mealplan.features.overview.MealPlanOverview
import com.pepper.mealplan.features.create.CreateMealPlan
import com.pepper.mealplan.data.order.MealOrderRepositoryProvider
import com.pepper.mealplan.features.face.FaceRecognitionScreen
import com.pepper.mealplan.features.order.OrderReminderScreen

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val title: String
) {
    object Overview : BottomNavItem("overview", Icons.Default.CalendarToday, "Wochenplanübersicht")
    object Create : BottomNavItem("create", Icons.Default.Add, "Mahlzeiten auswählen")
    object Logout : BottomNavItem("logout", Icons.Default.ExitToApp, "Abmelden")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Overview,
        BottomNavItem.Create,
        BottomNavItem.Logout
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val foundPerson = navBackStackEntry?.arguments?.getString("foundPerson") ?: ""

    // Repository für fehlende Bestellungen
    val repo = MealOrderRepositoryProvider.repository
    val missingCount = remember(foundPerson, currentRoute) {
        if (foundPerson.isNotBlank()) {
            repo.getMissingMealsForNextDays(foundPerson, days = 3).size
        } else {
            0
        }
    }

    var showMissingDialog by remember { mutableStateOf(false) }

    // BottomNav nur zeigen, wenn wir nicht auf FaceRecognition sind
    val showBottomNav = currentRoute != "face_recognition"

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                BottomNavigation(
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = MaterialTheme.colors.primary
                ) {
                    items.forEach { item ->
                        val isSelected = currentRoute?.startsWith(item.route) == true

                        BottomNavigationItem(
                            icon = {
                                if (item is BottomNavItem.Create && foundPerson.isNotBlank()) {
                                    if (missingCount > 0) {
                                        // Es fehlen Bestellungen → Add-Icon mit rotem "!"
                                        Box {
                                            Icon(
                                                item.icon,
                                                contentDescription = item.title
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .align(Alignment.TopEnd)
                                                    .offset(x = 4.dp, y = (-4).dp)
                                                    .background(Color.Red, shape = CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "!",
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    } else {
                                        // Alles bestellt → Grüner Haken
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Alle Bestellungen getätigt",
                                            tint = Color(0xFF2E7D32) // dunkles Grün
                                        )
                                    }
                                } else {
                                    Icon(
                                        item.icon,
                                        contentDescription = item.title
                                    )
                                }
                            },
                            label = {
                                if (item is BottomNavItem.Create && foundPerson.isNotBlank()) {
                                    when {
                                        missingCount > 0 -> {
                                            val text = if (missingCount == 1) {
                                                "1 Bestellung fehlt"
                                            } else {
                                                "$missingCount Bestellungen fehlen"
                                            }
                                            Text(
                                                text = text,
                                                color = Color.Red,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        else -> {
                                            // Alles bestellt
                                            Text(
                                                text = "Alle Bestellungen getätigt",
                                                color = Color(0xFF2E7D32),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                } else {
                                    Text(item.title)
                                }
                            },
                            selected = isSelected,
                            onClick = {
                                when (item) {
                                    is BottomNavItem.Logout -> {
                                        navController.navigate("face_recognition") {
                                            popUpTo(0) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }
                                    is BottomNavItem.Create -> {
                                        if (foundPerson.isBlank()) {
                                            // Fallback: Person unbekannt → normal zur Create-Seite
                                            navController.navigate("create/") {
                                                popUpTo(navController.graph.startDestinationId)
                                                launchSingleTop = true
                                            }
                                        } else if (missingCount > 0) {
                                            // Es fehlen Bestellungen → Dialog anzeigen
                                            showMissingDialog = true
                                        } else {
                                            // Alles bestellt → NICHT mehr navigieren
                                        }
                                    }
                                    is BottomNavItem.Overview -> {
                                        navController.navigate("overview/$foundPerson") {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            },
                            selectedContentColor = MaterialTheme.colors.primary,
                            unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "face_recognition",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("face_recognition") {
                FaceRecognitionScreen(
                    onAuthenticationSuccess = { person ->
                        val hasMissing =
                            repo.getMissingMealsForNextDays(person, days = 3).isNotEmpty()

                        if (hasMissing) {
                            navController.navigate("order_reminder/$person") {
                                popUpTo("face_recognition") { inclusive = true }
                            }
                        } else {
                            navController.navigate("overview/$person") {
                                popUpTo("face_recognition") { inclusive = true }
                            }
                        }
                    }
                )
            }
            composable("overview/{foundPerson}") { backStackEntry ->
                val person = backStackEntry.arguments?.getString("foundPerson") ?: ""
                MealPlanOverview(
                    foundPerson = person,
                    onGoToOrder = {
                        navController.navigate("create/$person") {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable("create/{foundPerson}") { backStackEntry ->
                val person = backStackEntry.arguments?.getString("foundPerson") ?: ""
                CreateMealPlan(foundPerson = person)
            }
            composable("order_reminder/{foundPerson}") { backStackEntry ->
                val person = backStackEntry.arguments?.getString("foundPerson") ?: ""
                OrderReminderScreen(
                    foundPerson = person,
                    onGoToOrder = {
                        navController.navigate("create/$person") {
                            launchSingleTop = true
                        }
                    },
                    onShowMenu = {
                        navController.navigate("overview/$person") {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }

    // ---------- Popup ----------
    if (showMissingDialog && missingCount > 0) {
        val text = if (missingCount == 1) {
            "Es fehlt noch 1 Bestellung in den nächsten Tagen."
        } else {
            "Es fehlen noch $missingCount Bestellungen in den nächsten Tagen."
        }

        AlertDialog(
            onDismissRequest = { showMissingDialog = false },
            title = {
                Text(
                    text = "Fehlende Bestellungen",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = text,
                    fontSize = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showMissingDialog = false
                        navController.navigate("create/$foundPerson") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary
                    )
                ) {
                    Text(text = "Jetzt bestellen", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showMissingDialog = false }
                ) {
                    Text(text = "Später")
                }
            }
        )
    }
}
