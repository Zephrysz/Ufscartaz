package com.ufscar.ufscartaz.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.navArgument
import androidx.navigation.NavType

// Import your actual Composable screen functions
import com.ufscar.ufscartaz.ui.screens.AvatarSelectionScreen
import com.ufscar.ufscartaz.ui.screens.LoginScreen
import com.ufscar.ufscartaz.ui.screens.RegistrationScreen
import com.ufscar.ufscartaz.ui.screens.SplashScreen
import com.ufscar.ufscartaz.ui.screens.WelcomeScreen
import com.ufscar.ufscartaz.ui.screens.MovieListScreen
import com.ufscar.ufscartaz.ui.screens.MovieDetailScreen


object AppDestinations {
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTRATION = "registration"
    const val AVATAR_SELECTION = "avatar_selection"
    const val MOVIES = "movies"

    const val MOVIE_DETAIL = "movie_detail"
    const val MOVIE_DETAIL_ROUTE = "$MOVIE_DETAIL/{movieId}"
    const val MOVIE_ID_ARG = "movieId"
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
        composable(AppDestinations.MOVIES) {
            MovieListScreen(navController = navController)
        }
        composable(
            route = AppDestinations.MOVIE_DETAIL_ROUTE,
            arguments = listOf(navArgument(AppDestinations.MOVIE_ID_ARG) { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt(AppDestinations.MOVIE_ID_ARG)
            if (movieId != null) {
                MovieDetailScreen(navController = navController, movieId = movieId)
            } else {
                navController.popBackStack()
            }
        }
    }
}