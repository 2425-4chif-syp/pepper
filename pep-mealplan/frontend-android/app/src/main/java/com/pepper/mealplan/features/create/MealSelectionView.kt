package com.pepper.mealplan.features.create

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pepper.mealplan.R

@Composable
fun MealSelectionView(
    weekNumber: Int,
    dayShort: String,
    mealStep: MealStep,
    onBackClick: () -> Unit,
    onMealSelected: (Int) -> Unit
) {
    val (title, imageRes, mealIds) = when (mealStep) {
        MealStep.SOUP -> Triple("Suppe", R.drawable.soup, getSoupIds())
        MealStep.MAIN -> Triple("Mittagsessen", R.drawable.main, getMainIds())
        MealStep.DESSERT -> Triple("Dessert", R.drawable.dessert, getDessertIds())
        MealStep.EVENING -> Triple("Abendessen", R.drawable.main, getEveningIds()) // Using main.jpg for evening too
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Zurück"
                )
            }
            
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Content based on meal step
        when (mealStep) {
            MealStep.SOUP, MealStep.DESSERT -> {
                // Single image for soup and dessert
                OptimizedImageCard(
                    imageRes = imageRes,
                    title = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp) // Reduziert von 400dp auf 250dp
                        .clickable { 
                            onMealSelected(mealIds.first())
                        }
                )
            }
            
            MealStep.MAIN, MealStep.EVENING -> {
                // Two images for main and evening meals
                Text(
                    text = "Wähle eine Option:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Option 1 (left) - uses main1.jpg
                    OptimizedImageCard(
                        imageRes = R.drawable.main1,
                        title = "Option 1",
                        modifier = Modifier
                            .weight(1f)
                            .height(200.dp) // Reduziert von 300dp auf 200dp
                            .clickable { 
                                onMealSelected(mealIds[0])
                            }
                    )
                    
                    // Option 2 (right) - uses main.jpg
                    OptimizedImageCard(
                        imageRes = R.drawable.main,
                        title = "Option 2",
                        modifier = Modifier
                            .weight(1f)
                            .height(200.dp) // Reduziert von 300dp auf 200dp
                            .clickable { 
                                onMealSelected(mealIds[1])
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun OptimizedImageCard(
    imageRes: Int,
    title: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Optimierte Bildladung mit reduzierten Optionen
    val bitmap = remember(imageRes) {
        val options = BitmapFactory.Options().apply {
            inSampleSize = 2 // Reduziert die Bildgröße um Faktor 2
            inPreferredConfig = android.graphics.Bitmap.Config.RGB_565 // Weniger Speicherverbrauch
        }
        
        try {
            val inputStream = context.resources.openRawResource(imageRes)
            val bmp = BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()
            bmp?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
    
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit // Geändert von Crop zu Fit
            )
        } else {
            // Fallback bei Ladeproblemen
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Helper functions to get meal IDs (simplified for now)
private fun getSoupIds(): List<Int> = listOf(11) // Soup IDs from DataInserts
private fun getMainIds(): List<Int> = listOf(1, 2) // First two main dish IDs
private fun getDessertIds(): List<Int> = listOf(21) // Dessert IDs
private fun getEveningIds(): List<Int> = listOf(3, 4) // Evening meal IDs