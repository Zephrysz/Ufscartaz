//package com.ufscar.ufscartaz.ui.screens
//
//import androidx.compose.ui.test.*
//import androidx.compose.ui.test.junit4.createAndroidComposeRule
//import androidx.navigation.compose.rememberNavController
//import com.ufscar.ufscartaz.MainActivity
//import com.ufscar.ufscartaz.data.model.Movie
//import com.ufscar.ufscartaz.ui.theme.UfscartazTheme
//import org.junit.Rule
//import org.junit.Test
//
//class MovieDetailScreenTest {
//
//    @get:Rule
//    val composeTestRule = createAndroidComposeRule<MainActivity>()
//
//    @Test
//    fun movieDetailScreen_showsOverviewText() {
//        val movie = Movie(
//            id = 1,
//            title = "Título de Teste",
//            overview = "Esta é uma sinopse de teste para ver se aparece.",
//            poster_path = "/poster.jpg",
//            genre_ids = listOf(28),
//            backdrop_path = "/backdrop.jpg",
//            vote_average = 7.5
//        )
//
//        composeTestRule.setContent {
//            UfscartazTheme {
//                MovieDetailScreen(
//                    navController = rememberNavController(),
//                    movieId = movie.id
//                )
//            }
//        }
//
//        // Aguarda a composição
//        composeTestRule.waitForIdle()
//
//        // Verifica se o texto da sinopse é exibido
//        composeTestRule.onNodeWithText("Esta é uma sinopse de teste para ver se aparece.")
//            .assertIsDisplayed()
//    }
//}
