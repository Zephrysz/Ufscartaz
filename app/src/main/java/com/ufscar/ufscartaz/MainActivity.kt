package com.ufscar.ufscartaz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.ufscar.ufscartaz.navigation.AppNavHost // Import your navigation composable
import com.ufscar.ufscartaz.ui.theme.UfscartazTheme // Your app's theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UfscartazTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()
    AppNavHost(navController = navController)
}