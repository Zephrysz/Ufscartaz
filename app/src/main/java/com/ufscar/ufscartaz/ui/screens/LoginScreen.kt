package com.ufscar.ufscartaz.ui.screens // Ensure this matches your project structure

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button // Or Material2 Button if you're using older Material
import androidx.compose.material3.OutlinedTextField // Or Material2 OutlinedTextField
import androidx.compose.material3.Text // Or Material2 Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview // For previewing
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel // Helper to get ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController // Needed for the Preview
import com.ufscar.ufscartaz.navigation.AppDestinations
import com.ufscar.ufscartaz.ui.theme.UfscartazTheme // Assuming you have a theme
//import com.ufscar.ufscartaz.viewmodels.AuthViewModel // Import your AuthViewModel

// This is your @Composable function representing the Login screen
@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Hello World - Login Screen")
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        
        Button(
            onClick = { navController.navigate(AppDestinations.HOME) {
                popUpTo(AppDestinations.LOGIN) { inclusive = true }
            }},
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Login")
        }
    }
}

// --- Preview Function ---
// This allows you to see the Composable in the Android Studio Preview pane
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    // Wrap your preview in your app's theme
    LoginScreen(navController = rememberNavController())
}