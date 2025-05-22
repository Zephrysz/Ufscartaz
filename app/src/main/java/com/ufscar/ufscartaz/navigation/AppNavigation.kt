package com.ufscar.ufscartaz.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

// Import your actual Composable screen functions
import com.ufscar.ufscartaz.ui.screens.AvatarSelectionScreen
import com.ufscar.ufscartaz.ui.screens.HomeScreen
import com.ufscar.ufscartaz.ui.screens.LoginScreen
import com.ufscar.ufscartaz.ui.screens.RegistrationScreen
import com.ufscar.ufscartaz.ui.screens.SplashScreen
import com.ufscar.ufscartaz.ui.screens.WelcomeScreen
import com.ufscar.ufscartaz.ui.screens.MovieListScreen

object AppDestinations {
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTRATION = "registration"
    const val AVATAR_SELECTION = "avatar_selection"
    const val HOME = "home"
    const val MOVIES = "movies"
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = AppDestinations.SPLASH) {
        composable(AppDestinations.SPLASH) {
            SplashScreen(navController = navController)
        }
        composable(AppDestinations.WELCOME) {
            WelcomeScreen(navController = navController)
        }
        composable(AppDestinations.LOGIN) {
            LoginScreen(navController = navController)
        }
        composable(AppDestinations.REGISTRATION) {
            RegistrationScreen(navController = navController)
        }
        composable(AppDestinations.AVATAR_SELECTION) {
            AvatarSelectionScreen(navController = navController)
        }
        composable(AppDestinations.HOME) {
            HomeScreen(navController = navController)
        }
        composable(AppDestinations.MOVIES) {
            MovieListScreen(navController = navController)
        }
    }
}