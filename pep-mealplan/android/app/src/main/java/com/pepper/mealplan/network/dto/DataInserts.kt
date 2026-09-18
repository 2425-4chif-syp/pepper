package com.pepper.mealplan.data

import com.pepper.mealplan.network.dto.*
import java.time.LocalDate

object DataInserts {

    fun createAllergens(): List<AllergenDto> {
        return listOf(
            AllergenDto("A", "Glutenhaltiges Getreide"),
            AllergenDto("B", "Krebstiere"),
            AllergenDto("C", "Eier"),
            AllergenDto("D", "Fische"),
            AllergenDto("E", "Erdnüsse"),
            AllergenDto("F", "Soja"),
            AllergenDto("G", "Milch"),
            AllergenDto("H", "Schalenfrüchte"),
            AllergenDto("L", "Sellerie"),
            AllergenDto("M", "Senf"),
            AllergenDto("N", "Sesam"),
            AllergenDto("O", "Schwefeldioxid und Sulfite"),
            AllergenDto("P", "Lupinen"),
            AllergenDto("R", "Weichtiere")
        )
    }

    fun createPeople(): List<PersonDto> {
        return listOf(
            PersonDto(firstname = "Stephanie", lastname = "Segebahn")
        )
    }

    fun createFoods(): List<FoodDto> {
        return listOf(
            // 10 Main dishes (main.jpg)
            FoodDto(id = 1, name = "Schweinsgeschnetzeltes mit Nudeln und gem. Salat", pictureId = null, type = "main"),
            FoodDto(id = 2, name = "Einmachhuhn mit Reis und Salat", pictureId = null, type = "main"),
            FoodDto(id = 3, name = "Spaghetti Carbonara mit gem. Salat", pictureId = null, type = "main"),
            FoodDto(id = 4, name = "Käsespätzle mit Salat", pictureId = null, type = "main"),
            FoodDto(id = 5, name = "Faschierter Braten mit Kartoffelpüree und Gemüse", pictureId = null, type = "main"),
            FoodDto(id = 6, name = "Grammelknödel mit Sauerkraut", pictureId = null, type = "main"),
            FoodDto(id = 7, name = "Reisfleisch mit Salat", pictureId = null, type = "main"),
            FoodDto(id = 8, name = "Knacker mit Rösti und Spinat", pictureId = null, type = "main"),
            FoodDto(id = 9, name = "Pizzanudeln mit Salat", pictureId = null, type = "main"),
            FoodDto(id = 10, name = "Hühner-Cordon-bleu mit Kartoffeln und Salat", pictureId = null, type = "main"),
            
            // 10 Soups (soup.jpg)
            FoodDto(id = 11, name = "Fleischstrudelsuppe", pictureId = null, type = "soup"),
            FoodDto(id = 12, name = "Backerbsensuppe", pictureId = null, type = "soup"),
            FoodDto(id = 13, name = "Zwiebelsuppe", pictureId = null, type = "soup"),
            FoodDto(id = 14, name = "Nudelsuppe", pictureId = null, type = "soup"),
            FoodDto(id = 15, name = "Gemüsesuppe", pictureId = null, type = "soup"),
            FoodDto(id = 16, name = "Grießsuppe", pictureId = null, type = "soup"),
            FoodDto(id = 17, name = "Leberknödelsuppe", pictureId = null, type = "soup"),
            FoodDto(id = 18, name = "Einbrennsuppe", pictureId = null, type = "soup"),
            FoodDto(id = 19, name = "Karottensuppe", pictureId = null, type = "soup"),
            FoodDto(id = 20, name = "Grießnockerlsuppe", pictureId = null, type = "soup"),
            
            // 10 Desserts (dessert.jpg)
            FoodDto(id = 21, name = "Topfenpalatschinken", pictureId = null, type = "dessert"),
            FoodDto(id = 22, name = "Topfenschmarrn mit Kompott", pictureId = null, type = "dessert"),
            FoodDto(id = 23, name = "Kaiserschmarrn mit Zwetschkenröster", pictureId = null, type = "dessert"),
            FoodDto(id = 24, name = "Apfelstrudel mit Vanillesauce", pictureId = null, type = "dessert"),
            FoodDto(id = 25, name = "Waldbeerjoghurt", pictureId = null, type = "dessert"),
            FoodDto(id = 26, name = "Kaffee und Kuchen", pictureId = null, type = "dessert"),
            FoodDto(id = 27, name = "Muffins", pictureId = null, type = "dessert"),
            FoodDto(id = 28, name = "Obstsalat", pictureId = null, type = "dessert"),
            FoodDto(id = 29, name = "Kekse", pictureId = null, type = "dessert"),
            FoodDto(id = 30, name = "Schnitten", pictureId = null, type = "dessert")
        )
    }

    fun createFoodAllergens(): List<FoodAllergenDto> {
        return listOf(
            // Topfenpalatschinken (ID 1) - A, C, G
            FoodAllergenDto(allergenShortname = "A", foodId = 1),
            FoodAllergenDto(allergenShortname = "C", foodId = 1),
            FoodAllergenDto(allergenShortname = "G", foodId = 1),
            
            // Schweinsgeschnetzeltes mit Nudeln und gem. Salat (ID 2) - A, G
            FoodAllergenDto(allergenShortname = "A", foodId = 2),
            FoodAllergenDto(allergenShortname = "G", foodId = 2),
            
            // Topfenschmarrn mit Kompott (ID 3) - A, C, G
            FoodAllergenDto(allergenShortname = "A", foodId = 3),
            FoodAllergenDto(allergenShortname = "C", foodId = 3),
            FoodAllergenDto(allergenShortname = "G", foodId = 3),
        )
    }

    fun createMenus(): List<MenuDto> {
        return listOf(
            // Woche 1
            MenuDto(weekNumber = 1, weekday = "MO", soupId = 11, m1Id = 1, m2Id = 2, lunchDessertId = 21, a1Id = 3, a2Id = 4),
            MenuDto(weekNumber = 1, weekday = "DI", soupId = 12, m1Id = 3, m2Id = 4, lunchDessertId = 22, a1Id = 5, a2Id = 6),
            MenuDto(weekNumber = 1, weekday = "MI", soupId = 13, m1Id = 5, m2Id = 6, lunchDessertId = 23, a1Id = 7, a2Id = 8),
            MenuDto(weekNumber = 1, weekday = "DO", soupId = 14, m1Id = 7, m2Id = 8, lunchDessertId = 24, a1Id = 9, a2Id = 10),
            MenuDto(weekNumber = 1, weekday = "FR", soupId = 15, m1Id = 9, m2Id = 10, lunchDessertId = 25, a1Id = 1, a2Id = 2),
            MenuDto(weekNumber = 1, weekday = "SA", soupId = 16, m1Id = 1, m2Id = 3, lunchDessertId = 26, a1Id = 5, a2Id = 7),
            MenuDto(weekNumber = 1, weekday = "SO", soupId = 17, m1Id = 2, m2Id = 4, lunchDessertId = 27, a1Id = 6, a2Id = 8),

            // Woche 2
            MenuDto(weekNumber = 2, weekday = "MO", soupId = 18, m1Id = 4, m2Id = 6, lunchDessertId = 28, a1Id = 9, a2Id = 10),
            MenuDto(weekNumber = 2, weekday = "DI", soupId = 19, m1Id = 8, m2Id = 10, lunchDessertId = 29, a1Id = 1, a2Id = 3),
            MenuDto(weekNumber = 2, weekday = "MI", soupId = 20, m1Id = 2, m2Id = 5, lunchDessertId = 30, a1Id = 7, a2Id = 9),
            MenuDto(weekNumber = 2, weekday = "DO", soupId = 11, m1Id = 6, m2Id = 9, lunchDessertId = 21, a1Id = 2, a2Id = 4),
            MenuDto(weekNumber = 2, weekday = "FR", soupId = 12, m1Id = 1, m2Id = 7, lunchDessertId = 22, a1Id = 8, a2Id = 10),
            MenuDto(weekNumber = 2, weekday = "SA", soupId = 13, m1Id = 3, m2Id = 8, lunchDessertId = 23, a1Id = 5, a2Id = 6),
            MenuDto(weekNumber = 2, weekday = "SO", soupId = 14, m1Id = 10, m2Id = 1, lunchDessertId = 24, a1Id = 4, a2Id = 7),

            // Woche 3
            MenuDto(weekNumber = 3, weekday = "MO", soupId = 15, m1Id = 5, m2Id = 9, lunchDessertId = 25, a1Id = 3, a2Id = 8),
            MenuDto(weekNumber = 3, weekday = "DI", soupId = 16, m1Id = 7, m2Id = 2, lunchDessertId = 26, a1Id = 6, a2Id = 10),
            MenuDto(weekNumber = 3, weekday = "MI", soupId = 17, m1Id = 4, m2Id = 1, lunchDessertId = 27, a1Id = 9, a2Id = 5),
            MenuDto(weekNumber = 3, weekday = "DO", soupId = 18, m1Id = 8, m2Id = 3, lunchDessertId = 28, a1Id = 2, a2Id = 7),
            MenuDto(weekNumber = 3, weekday = "FR", soupId = 19, m1Id = 6, m2Id = 10, lunchDessertId = 29, a1Id = 1, a2Id = 4),
            MenuDto(weekNumber = 3, weekday = "SA", soupId = 20, m1Id = 9, m2Id = 5, lunchDessertId = 30, a1Id = 8, a2Id = 3),
            MenuDto(weekNumber = 3, weekday = "SO", soupId = 11, m1Id = 2, m2Id = 7, lunchDessertId = 21, a1Id = 6, a2Id = 10),

            // Woche 4
            MenuDto(weekNumber = 4, weekday = "MO", soupId = 12, m1Id = 1, m2Id = 4, lunchDessertId = 22, a1Id = 5, a2Id = 9),
            MenuDto(weekNumber = 4, weekday = "DI", soupId = 13, m1Id = 10, m2Id = 6, lunchDessertId = 23, a1Id = 7, a2Id = 2),
            MenuDto(weekNumber = 4, weekday = "MI", soupId = 14, m1Id = 3, m2Id = 8, lunchDessertId = 24, a1Id = 1, a2Id = 4),
            MenuDto(weekNumber = 4, weekday = "DO", soupId = 15, m1Id = 5, m2Id = 9, lunchDessertId = 25, a1Id = 8, a2Id = 6),
            MenuDto(weekNumber = 4, weekday = "FR", soupId = 16, m1Id = 7, m2Id = 1, lunchDessertId = 26, a1Id = 3, a2Id = 10),
            MenuDto(weekNumber = 4, weekday = "SA", soupId = 17, m1Id = 2, m2Id = 10, lunchDessertId = 27, a1Id = 9, a2Id = 5),
            MenuDto(weekNumber = 4, weekday = "SO", soupId = 18, m1Id = 4, m2Id = 8, lunchDessertId = 28, a1Id = 7, a2Id = 1)
        )
    }

    fun getAllData(): DataInsertCollection {
        return DataInsertCollection(
            allergens = createAllergens(),
            people = createPeople(),
            foods = createFoods(),
            foodAllergens = createFoodAllergens(),
            menus = createMenus()
        )
    }
}

data class DataInsertCollection(
    val allergens: List<AllergenDto>,
    val people: List<PersonDto>,
    val foods: List<FoodDto>,
    val foodAllergens: List<FoodAllergenDto>,
    val menus: List<MenuDto>
)