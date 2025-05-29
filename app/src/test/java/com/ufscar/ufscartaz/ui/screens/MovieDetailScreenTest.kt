//package com.ufscar.ufscartaz.ui.screens
//
//import androidx.compose.runtime.State
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.ui.test.* // Import Compose testing functions
//import androidx.compose.ui.test.junit4.createComposeRule // Import createComposeRule
//import androidx.lifecycle.ViewModel
//import androidx.navigation.NavController // Mocking NavController
//import androidx.navigation.compose.ComposeNavigator // Required for TestNavHostController
//import androidx.navigation.testing.TestNavHostController // For testing navigation
//import com.ufscar.ufscartaz.R // Access string resources
//import com.ufscar.ufscartaz.data.model.Movie
//import com.ufscar.ufscartaz.ui.viewmodels.MovieViewModel // The actual ViewModel
//import com.ufscar.ufscartaz.data.model.getGenreNames // Extension function
//
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.Mock
//import org.mockito.Mockito.* // Import Mockito functions
//import org.mockito.MockitoAnnotations
//
//// Use AndroidJUnit4 for running on device/emulator
//@RunWith(AndroidJUnit4::class)
//class MovieDetailScreenTest {
//
//    // Rule for Compose testing
//    @get:Rule
//    val composeTestRule = createComposeRule()
//
//    // Mock ViewModel or create a test implementation
//    // We need to control the state flows for testing different scenarios
//    // Let's create a test implementation that allows setting the state.
//    class TestMovieViewModel : ViewModel() {
//        private val _movies = mutableStateOf<List<Movie>>(emptyList())
//        val movies: State<List<Movie>> = _movies // Expose as State for simpler testing
//
//        private val _isLoading = mutableStateOf(false)
//        val isLoading: State<Boolean> = _isLoading
//
//        // Need mock functions that might be called (even if they do nothing in the test)
//        fun loadPopularMovies() { /* Do nothing */ }
//        fun recordMovieClick(movieId: Int) { /* Do nothing */ }
//        // Other ViewModel methods as needed by the screen
//
//        // Helper functions to set state for tests
//        fun setMovies(movieList: List<Movie>) {
//            _movies.value = movieList
//        }
//
//        fun setIsLoading(loading: Boolean) {
//            _isLoading.value = loading
//        }
//    }
//
//    // Test ViewModel instance
//    private lateinit var testViewModel: TestMovieViewModel
//
//    // Test NavController
//    private lateinit var testNavController: TestNavHostController
//
//    // Dummy movie data for tests
//    private val dummyMovie = Movie(
//        id = 123,
//        title = "Test Movie Title",
//        overview = "This is a test overview for the movie.",
//        poster_path = "/test_poster.jpg",
//        genre_ids = listOf(28, 12), // Action, Adventure
//        backdrop_path = "/test_backdrop.jpg",
//        vote_average = 8.5
//    )
//
//    @Before
//    fun setup() {
//        // Initialize the test ViewModel
//        testViewModel = TestMovieViewModel()
//
//        // Initialize the Test NavController
//        // Must set a Navigator to avoid exception
//        composeTestRule.activity.runOnUiThread {
//            testNavController = TestNavHostController(composeTestRule.activity)
//            testNavController.navigatorProvider.addNavigator(ComposeNavigator())
//            // Optionally set a start destination if needed for complex navigation tests
//        }
//    }
//
//    @Test
//    fun movieDetails_DisplayedCorrectly_WhenMovieFound() {
//        // Arrange: Set the ViewModel state to contain the dummy movie and not be loading
//        testViewModel.setMovies(listOf(dummyMovie))
//        testViewModel.setIsLoading(false)
//
//        // Arrange: Set Compose content with the screen and the mock ViewModel
//        composeTestRule.setContent {
//            MovieDetailScreen(navController = testNavController, movieId = dummyMovie.id, viewModel = testViewModel)
//        }
//
//        // Assert: Verify key details are displayed
//        // Check Title (in TopAppBar)
//        composeTestRule
//            .onNodeWithText(dummyMovie.title, substring = true) // Use substring=true if title is truncated
//            .assertExists()
//
//        // Check Title (larger one below backdrop)
//        composeTestRule
//            .onNodeWithText(dummyMovie.title, substring = true) // Should find the same text again
//            .assertExists()
//
//
//        // Check Overview
//        composeTestRule
//            .onNodeWithText(dummyMovie.overview, substring = true)
//            .assertExists()
//
//        // Check Rating
//        // Need to load the string resource from R.string.rating
//        val ratingString = composeTestRule.activity.getString(R.string.rating, String.format("%.1f", dummyMovie.vote_average))
//        composeTestRule
//            .onNodeWithText(ratingString, substring = true)
//            .assertExists()
//
//        // Check Genres (check for parts of genre names)
//        val genreNames = dummyMovie.getGenreNames()
//        genreNames.forEach { genreName ->
//            composeTestRule
//                .onNodeWithText(genreName, substring = true)
//                .assertExists()
//        }
//        // Check the separator • is present (can be tricky with text splitting, check row structure if needed)
//
//        // Check images are present (using content description or role)
//        // Note: AsyncImage doesn't always have a specific test role by default,
//        // checking contentDescription is reliable if set.
//        // contentDescription = movie?.title
//        composeTestRule
//            .onNodeWithContentDescription(dummyMovie.title)
//            .assertExists() // This should find both the backdrop and poster if they have the same CD
//
//        // Verify loading indicator is not shown
//        composeTestRule
//            .onNodeWithContentDescription(composeTestRule.activity.getString(R.string.loading)) // Assuming R.string.loading is used for loading CD
//            .assertDoesNotExist()
//
//        // Verify movie not found message is not shown
//        composeTestRule
//            .onNodeWithText(composeTestRule.activity.getString(R.string.movie_not_found))
//            .assertDoesNotExist()
//    }
//
//    @Test
//    fun loadingIndicator_Displayed_WhenLoading() {
//        // Arrange: Set the ViewModel state to be loading and movies list empty
//        testViewModel.setMovies(emptyList())
//        testViewModel.setIsLoading(true)
//
//        // Arrange: Set Compose content
//        composeTestRule.setContent {
//            // Passing a valid ID, but the data won't be found yet because isLoading is true
//            MovieDetailScreen(navController = testNavController, movieId = dummyMovie.id, viewModel = testViewModel)
//        }
//
//        // Assert: Verify loading indicator is shown
//        composeTestRule
//            .onNodeWithContentDescription(composeTestRule.activity.getString(R.string.loading)) // Assuming loading indicator uses this CD or text
//            .assertExists()
//
//        // Verify movie details are not shown
//        composeTestRule
//            .onNodeWithText(dummyMovie.title, substring = true)
//            .assertDoesNotExist()
//        composeTestRule
//            .onNodeWithText(dummyMovie.overview, substring = true)
//            .assertDoesNotExist()
//        composeTestRule
//            .onNodeWithText(composeTestRule.activity.getString(R.string.movie_not_found))
//            .assertDoesNotExist()
//    }
//
//    @Test
//    fun movieNotFoundMessage_Displayed_WhenMovieNotInListAfterLoading() {
//        // Arrange: Set the ViewModel state to not be loading and movies list does NOT contain the target movie
//        testViewModel.setMovies(createDummyMovies(5)) // Some other movies
//        testViewModel.setIsLoading(false)
//
//        // Arrange: Set Compose content, requesting an ID that is NOT in the set movies
//        val nonExistentMovieId = 9999
//        composeTestRule.setContent {
//            MovieDetailScreen(navController = testNavController, movieId = nonExistentMovieId, viewModel = testViewModel)
//        }
//
//        // Assert: Verify movie not found message is shown
//        composeTestRule
//            .onNodeWithText(composeTestRule.activity.getString(R.string.movie_not_found))
//            .assertExists()
//
//        // Verify loading indicator is not shown
//        composeTestRule
//            .onNodeWithContentDescription(composeTestRule.activity.getString(R.string.loading))
//            .assertDoesNotExist()
//
//        // Verify movie details are not shown
//        composeTestRule
//            .onNodeWithText(dummyMovie.title, substring = true) // Check dummy title is not present
//            .assertDoesNotExist()
//
//        // Verify the "Voltar" button is visible in the "Movie Not Found" state
//        composeTestRule
//            .onNodeWithText(composeTestRule.activity.getString(R.string.back)) // Assuming "Voltar" uses this string
//            .assertExists()
//    }
//
//    @Test
//    fun backButton_NavigatesUp() {
//        // Arrange: Set the ViewModel state with a movie
//        testViewModel.setMovies(listOf(dummyMovie))
//        testViewModel.setIsLoading(false)
//
//        // Arrange: Set Compose content with the screen and the TestNavController
//        composeTestRule.setContent {
//            MovieDetailScreen(navController = testNavController, movieId = dummyMovie.id, viewModel = testViewModel)
//        }
//
//        // Act: Click the back button
//        composeTestRule
//            .onNodeWithContentDescription(composeTestRule.activity.getString(R.string.back)) // Content description for back arrow
//            .performClick()
//
//        // Assert: Verify that navController.popBackStack() was called
//        // TestNavHostController doesn't automatically pop, but you can verify its state or use a mock and verify the call.
//        // Verifying the route stack is a way:
//        // You need to ensure the navController starts on the detail screen
//        // A simpler method is often to use a Mock NavController and verify the call.
//
//        // --- Using Mock NavController (alternative setup) ---
//        @Mock lateinit var mockNavController: NavController
//        @Before fun setupWithMockNav() {
//            MockitoAnnotations.openMocks(this)
//            testViewModel = TestMovieViewModel() // Use the same test VM
//            testViewModel.setMovies(listOf(dummyMovie))
//            testViewModel.setIsLoading(false)
//            composeTestRule.setContent {
//                // Provide the mock NavController
//                MovieDetailScreen(navController = mockNavController, movieId = dummyMovie.id, viewModel = testViewModel)
//            }
//        }
//        @Test fun backButton_NavigatesUp_using_mock() {
//            // This test would go in a separate test class or use conditional setup
//            // Assuming setupWithMockNav ran:
//            composeTestRule
//                .onNodeWithContentDescription(composeTestRule.activity.getString(R.string.back))
//                .performClick()
//
//            // Verify popBackStack was called
//            verify(mockNavController).popBackStack()
//        }
//        // --- End Mock NavController ---
//
//        // For the TestNavHostController, verifying the stack size change is possible but more involved.
//        // Let's assume the Mock NavController approach is preferred for verifying specific calls like popBackStack().
//        // If sticking with TestNavHostController, you'd navigate *to* the detail screen within the test setup
//        // and then assert the stack size or destination after clicking back.
//        // Example (more complex):
//        // composeTestRule.activity.runOnUiThread { testNavController.navigate("some_start_route") }
//        // composeTestRule.activity.runOnUiThread { testNavController.navigate("${AppDestinations.MOVIE_DETAIL}/${dummyMovie.id}") }
//        // composeTestRule.waitForIdle() // Wait for navigation/recomposition
//        // val initialStackSize = testNavController.currentBackStack.value.size
//        // composeTestRule.onNodeWithContentDescription(...).performClick()
//        // composeTestRule.waitForIdle()
//        // assertEquals(initialStackSize - 1, testNavController.currentBackStack.value.size) // Verify stack size decreased
//    }
//
//    // Add more tests for other UI elements, empty image handling, etc.
//}
//
//// --- Helper function to create dummy movies (can be shared) ---
//private fun createDummyMovies(count: Int): List<Movie> {
//    return (1..count).map { i ->
//        Movie(
//            id = i,
//            title = "Movie $i",
//            overview = "Overview $i",
//            poster_path = "/p$i.jpg",
//            genre_ids = listOf(i % 5, (i + 1) % 5),
//            backdrop_path = "/b$i.jpg", // Add backdrop for testing
//            vote_average = i.toDouble() * 2
//        )
//    }
//}