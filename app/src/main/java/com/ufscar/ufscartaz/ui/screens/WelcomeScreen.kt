package com.ufscar.ufscartaz.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ufscar.ufscartaz.navigation.AppDestinations

@Composable
fun WelcomeScreen(navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Hello World - Welcome Screen")
            
            Button(
                onClick = { navController.navigate(AppDestinations.LOGIN) },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Login")
            }
            
            Button(
                onClick = { navController.navigate(AppDestinations.REGISTRATION) },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(text = "Register")
            }
        }
    }
}