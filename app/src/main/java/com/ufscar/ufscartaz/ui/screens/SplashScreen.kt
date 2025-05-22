package com.ufscar.ufscartaz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ufscar.ufscartaz.R
import com.ufscar.ufscartaz.navigation.AppDestinations
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    // Auto-navigation to Welcome screen after 2 seconds
    LaunchedEffect(key1 = true) {
        delay(2000) // 2 seconds delay
        navController.navigate(AppDestinations.WELCOME) {
            popUpTo(AppDestinations.SPLASH) { inclusive = true }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Use annotated string to style the two parts of the logo text
        val text = buildAnnotatedString {
            withStyle(style = SpanStyle(
                color = Color.Red,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )) {
                append(stringResource(R.string.logo_part1))
            }
            withStyle(style = SpanStyle(
                color = Color.Red,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )) {
                append(stringResource(R.string.logo_part2))
            }
        }
        
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(navController = rememberNavController())
}