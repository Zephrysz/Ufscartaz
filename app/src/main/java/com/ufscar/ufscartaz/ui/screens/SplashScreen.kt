package com.ufscar.ufscartaz.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.ufscar.ufscartaz.navigation.AppDestinations
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    // Simple splash screen with auto-navigation to Welcome screen after 2 seconds
    LaunchedEffect(key1 = true) {
        delay(2000) // 2 seconds delay
        navController.navigate(AppDestinations.WELCOME) {
            popUpTo(AppDestinations.SPLASH) { inclusive = true }
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Hello World - Splash Screen")
    }
}