package com.ufscar.ufscartaz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.ufscar.ufscartaz.R
import com.ufscar.ufscartaz.data.model.Movie
import com.ufscar.ufscartaz.data.model.getGenreNamesComposable
import com.ufscar.ufscartaz.ui.viewmodels.MovieViewModel
import android.util.Log // Import Log


private const val TAG = "MovieDetailScreen" // Tag for logging

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    navController: NavHostController,
    movieId: Int,
    viewModel: MovieViewModel = viewModel()
) {
    Log.d(TAG, "Composing MovieDetailScreen for movie ID: $movieId")

    val movies by viewModel.movies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState() // Observe loading state directly here too

    Log.d(TAG, "ViewModel movies state updated. Size: ${movies.size}, IsLoading: $isLoading")


    val movie = remember(movies, movieId) {
        val foundMovie = movies.find { it.id == movieId }
        Log.d(TAG, "Finding movie ID $movieId. Found: ${foundMovie != null}")
        foundMovie
    }

    val movieFound = movie != null

    Log.d(TAG, "Movie found state: $movieFound")

    Scaffold(
        containerColor = Color.Black, // Background color
        topBar = {
            TopAppBar(
                title = {
                    // Display the movie title, truncated if needed, when available
                    if (movieFound) {
                        Text(
                            text = movie?.title ?: stringResource(R.string.loading),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        // Show a generic title or loading indicator if movie not found yet
                        Text(stringResource(R.string.loading), color = Color.White) // Add R.string.loading
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back), // Add R.string.back
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black // Top bar background
                )
            )
        }
    ) { paddingValues ->
        // Use a Box to handle alignment if movie not found
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
        ) {
            when {
                // Case 1: Movie data is not found (either null initially or after loading)
                !movieFound && !viewModel.isLoading.collectAsState().value -> { // Also check if loading finished
                    Log.d(TAG, "Showing Loading state")
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.movie_not_found), // Add R.string.movie_not_found
                            color = Color.Red,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.popBackStack() }) {
                            Text(stringResource(R.string.back)) // Reuse or add R.string.back_button
                        }
                    }
                }
                // Case 2: Loading is in progress
                viewModel.isLoading.collectAsState().value && !movieFound -> {
                    Log.d(TAG, "Showing Movie Not Found state")
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                // Case 3: Movie data is found, display details
                movieFound -> {
                    Log.d(TAG, "Showing Movie Details for: ${movie?.title}")
                    // Use verticalScroll to make the content scrollable
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()) // Make content scrollable
                    ) {
                        // Backdrop Image (or Poster if backdrop is null)
                        AsyncImage(
                            model = if (movie!!.backdrop_path != null)
                                "https://image.tmdb.org/t/p/w1280${movie.backdrop_path}" // Use a larger size for backdrop
                            else
                                "https://image.tmdb.org/t/p/w780${movie.poster_path}", // Fallback to poster
                            contentDescription = movie.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp) // Adjust height as needed
                        )

                        // Movie Info Section
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Title (already in TopAppBar, but maybe repeat here larger)
                            Text(
                                text = movie.title,
                                color = Color.White,
                                fontSize = 24.sp, // Larger title here
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Genres
                            Row(
                                modifier = Modifier.padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val genres = movie.getGenreNamesComposable()
                                genres.forEachIndexed { index, genre ->
                                    if (index > 0) {
                                        Text(
                                            text = "•",
                                            color = Color.Red, // Adjust color
                                            fontSize = 14.sp
                                        )
                                    }
                                    Text(
                                        text = genre,
                                        color = Color.LightGray, // Adjust color
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            // Rating (Optional)
                            if (movie.vote_average > 0) {
                                Text(
                                    text = stringResource(R.string.rating, movie.vote_average),
                                    color = Color.Yellow, // Adjust color
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }

                            // Overview/Synopsis
                            Text(
                                text = stringResource(R.string.overview_title), // Add R.string.overview_title "Sinopse"
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = movie.overview ?: stringResource(R.string.no_overview_available), // Add R.string.no_overview_available "Sinopse não disponível"
                                color = Color.LightGray, // Adjust color
                                fontSize = 14.sp,
                                lineHeight = 20.sp // Improve readability
                            )

                            // Add more details here (cast, release date, etc.) if available in your Movie model or fetched separately
                        }
                    }
                }
            }
        }
    }
}

// Add a preview for the detail screen
@Preview(showBackground = true)
@Composable
fun MovieDetailScreenPreview() {
    // Create a dummy movie for the preview
    val dummyMovie = Movie(
        id = 1,
        title = "Filme de Teste Longo Para Sinopse",
        overview = "Esta é uma sinopse de teste para o filme de exemplo. Ela deve ser longa o suficiente para testar o TextOverflow. Aqui está mais texto para garantir que ele seja truncado se necessário.",
        poster_path = "/path/to/poster.jpg", // Use a placeholder image URL if possible
        genre_ids = listOf(28, 12, 16),
        backdrop_path = "/path/to/backdrop.jpg", // Use a placeholder image URL if possible
        vote_average = 7.5
    )
    // Need to provide the movie data to the ViewModel for the preview
    // This requires modifying the ViewModel or using a mock ViewModel for previews
    // For simplicity in preview, we'll just render the screen structure assuming data exists

    // Note: Previews for screens that rely heavily on ViewModel state passed via navigation
    // are tricky. This preview only shows the structure *if* movie data is available.
    // A more robust preview would inject a mock ViewModel or pass dummy data directly.

    MovieDetailScreen(navController = rememberNavController(), movieId = dummyMovie.id)
}