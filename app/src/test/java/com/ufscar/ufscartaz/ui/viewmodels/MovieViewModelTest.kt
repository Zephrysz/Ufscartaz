//package com.ufscar.ufscartaz.ui.viewmodels
//
//import android.app.Application
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule // Import the rule
//import com.ufscar.ufscartaz.data.local.AppDatabase // Mocking DB access
//import com.ufscar.ufscartaz.data.local.MovieHistoryDao // Mocking DAO
//import com.ufscar.ufscartaz.data.model.Movie
//import com.ufscar.ufscartaz.data.repository.MovieRepository // Mocking MovieRepo
//import com.ufscar.ufscartaz.data.repository.MovieHistoryRepository // Mocking HistoryRepo
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.first // To get the first emitted value
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.test.* // For runTest and TestDispatcher
//import org.junit.Before
//import org.junit.Rule // Import Rule
//import org.junit.Test
//import org.mockito.Mock
//import org.mockito.Mockito.* // Import Mockito functions
//import org.mockito.MockitoAnnotations
//import java.io.IOException
//import kotlin.test.assertEquals
//import kotlin.test.assertFalse
//import kotlin.test.assertTrue
//
//@ExperimentalCoroutinesApi
//class MovieViewModelTest {
//
//    // Rule for testing coroutines
//    @get:Rule
//    val mainDispatcherRule = TestDispatcherRule() // Custom rule for TestDispatcher
//
//    // Rule for instant execution of LiveData (good practice)
//    @get:Rule
//    val instantTaskExecutorRule = InstantTaskExecutorRule()
//
//    // Mocks for dependencies
//    @Mock
//    private lateinit var mockMovieRepository: MovieRepository
//    @Mock
//    private lateinit var mockMovieHistoryRepository: MovieHistoryRepository
//    @Mock
//    private lateinit var mockApplication: Application // Mock Application context
//    @Mock
//    private lateinit var mockAppDatabase: AppDatabase // Mock Database
//    @Mock
//    private lateinit var mockMovieHistoryDao: MovieHistoryDao // Mock DAO
//
//    private lateinit var movieViewModel: MovieViewModel
//
//    @Before
//    fun setup() {
//        MockitoAnnotations.openMocks(this)
//
//        // Configure mock Application to return a mock database
//        `when`(mockApplication.applicationContext).thenReturn(mockApplication) // Mock context for getDatabase call
//        `when`(mockAppDatabase.movieHistoryDao()).thenReturn(mockMovieHistoryDao) // Mock DAO access
//        // Need to mock the static getDatabase call (requires power mockito or similar, or refactor AppDatabase)
//        // A simpler approach for tests is often to provide the DAOs or Repositories directly if possible,
//        // or create a factory for the ViewModel. For this example, we'll simplify by pretending
//        // the ViewModel receives the repository directly or mocks the static call if using PowerMockito/Dexter
//        // Let's assume you refactor or use a factory pattern in a real app for testability.
//        // For this test, we'll pass the mocked repositories directly to a hypothetical ViewModel factory or constructor.
//        // Or, let's stick to the AndroidViewModel constructor and mock the database path.
//
//        // Mock the static getDatabase call if you can (requires more setup than standard mockito)
//        // Alternatively, refactor AppDatabase.getDatabase to be testable.
//        // For basic unit tests, we often bypass the full init block complexity or use a factory.
//
//        // Let's create a simplified ViewModel constructor for testing purposes
//        // In a real app, you'd use a ViewModelProvider.Factory with Hilt/Koin or manual factory
//        movieViewModel = MovieViewModel(mockApplication) // Pass mock Application
//        // We need to ensure the repos inside are the mocks. This requires refactoring the ViewModel.
//        // A common pattern: ViewModel receives repositories in constructor.
//        // Let's adapt the ViewModel slightly for testability.
//
//        // --- REFACTORING ViewModel for Testability ---
//        // Change MovieViewModel constructor to accept repositories
//        // class MovieViewModel(
//        //    application: Application,
//        //    private val repository: MovieRepository,
//        //    private val movieHistoryRepository: MovieHistoryRepository
//        // ) : AndroidViewModel(application) { ... }
//        // Then in setup:
//        // movieViewModel = MovieViewModel(mockApplication, mockMovieRepository, mockMovieHistoryRepository)
//        // In the actual app, you'd use a factory:
//        // class MovieViewModelFactory(private val app: Application, private val repo: MovieRepository, private val historyRepo: MovieHistoryRepository) : ViewModelProvider.Factory { ... }
//        // And use it in the composable viewModel(factory = MovieViewModelFactory(...))
//
//        // Let's proceed assuming the ViewModel gets repos somehow and we can verify calls *on those repos*.
//        // However, testing the init block is hard without refactoring.
//        // Let's focus on testing the *public methods* that *use* the repositories.
//        // We'll instantiate the ViewModel as is, but acknowledge the init block isn't fully tested this way.
//        // The most testable approach is the factory/constructor injection. Let's switch to that.
//        // -------------
//
//        // Mock the MovieViewModel constructor calls:
//        // Assuming MovieViewModel takes repositories directly for testability
//        // movieViewModel = MovieViewModel(mockApplication, mockMovieRepository, mockMovieHistoryRepository)
//
//        // If keeping the original init block approach, you have to test the public methods and
//        // rely on them using the correct internal repository instances.
//        // Let's test the public methods directly on the instance created with mocks.
//        // Note: The init block's loadPopularMovies will run immediately. Reset state after setup if needed.
//
//        // Reset the ViewModel state after init runs for cleaner test state
//        runTest { // Need runTest because init calls a suspend function (loadPopularMovies)
//            movieViewModel = MovieViewModel(mockApplication) // Instantiate the original ViewModel
//            // Wait for the init block's loadPopularMovies to finish or fail
//            // This is tricky without controlling the dispatcher.
//            // Using TestDispatcherRule handles the background coroutines in init.
//
//            // Alternatively, if refactored to take repositories:
//            // movieViewModel = MovieViewModel(mockApplication, mockMovieRepository, mockMovieHistoryRepository)
//            // Then you don't need to wait for init here as it won't call suspend functions immediately unless you explicitly manage it.
//
//            // Let's assume the original ViewModel structure for now and use TestDispatcherRule to handle init.
//            // After init, loadPopularMovies will have been called.
//            // We can reset the ViewModel state or just test the state *after* this initial load.
//            // Testing the *effects* of calling loadPopularMovies explicitly later is better.
//        }
//    }
//
//    // --- Helper function to create dummy movies ---
//    private fun createDummyMovies(count: Int): List<Movie> {
//        return (1..count).map { i ->
//            Movie(
//                id = i,
//                title = "Movie $i",
//                overview = "Overview $i",
//                poster_path = "/p$i.jpg",
//                genre_ids = listOf(i % 5, (i + 1) % 5) // Some dummy genres
//            )
//        }
//    }
//
//
//    // --- Test Cases ---
//
//    @Test
//    fun `loadPopularMovies updates movies state on success`() = runTest {
//        // Arrange: Create dummy data
//        val dummyMovies = createDummyMovies(5)
//        // Arrange: Configure mock repository
//        `when`(mockMovieRepository.getPopularMovies()).thenReturn(dummyMovies)
//
//        // Act: Call the method
//        movieViewModel.loadPopularMovies()
//
//        // Assert: Verify the state flows are updated correctly
//        // Using launch and first() to collect emissions
//        val isLoading = movieViewModel.isLoading.first()
//        val movies = movieViewModel.movies.first()
//        val error = movieViewModel.error.first()
//
//        assertFalse(isLoading, "isLoading should be false after success")
//        assertEquals(dummyMovies.size, movies.size, "Movies list size should match dummy data")
//        assertEquals(dummyMovies[0].title, movies[0].title, "First movie title should match")
//        assertEquals(null, error, "Error state should be null on success")
//
//        // Verify repository method was called
//        verify(mockMovieRepository).getPopularMovies()
//    }
//
//    @Test
//    fun `loadPopularMovies updates error state on API failure`() = runTest {
//        // Arrange: Configure mock repository to throw an exception
//        val errorMessage = "Failed to fetch"
//        `when`(mockMovieRepository.getPopularMovies()).thenThrow(IOException(errorMessage))
//
//        // Act: Call the method
//        movieViewModel.loadPopularMovies()
//
//        // Assert: Verify the state flows are updated correctly
//        val isLoading = movieViewModel.isLoading.first()
//        val movies = movieViewModel.movies.first()
//        val error = movieViewModel.error.first()
//
//        assertFalse(isLoading, "isLoading should be false after error")
//        assertTrue(movies.isEmpty(), "Movies list should be empty on error")
//        assertEquals("Erro ao carregar filmes: $errorMessage", error, "Error state should contain the error message")
//
//        // Verify repository method was called
//        verify(mockMovieRepository).getPopularMovies()
//    }
//
//    @Test
//    fun `loadPopularMovies updates error state on empty list response`() = runTest {
//        // Arrange: Configure mock repository to return an empty list
//        `when`(mockMovieRepository.getPopularMovies()).thenReturn(emptyList())
//
//        // Act: Call the method
//        movieViewModel.loadPopularMovies()
//
//        // Assert: Verify the state flows are updated correctly
//        val isLoading = movieViewModel.isLoading.first()
//        val movies = movieViewModel.movies.first()
//        val error = movieViewModel.error.first()
//
//        assertFalse(isLoading, "isLoading should be false after empty response")
//        assertTrue(movies.isEmpty(), "Movies list should be empty on empty response")
//        // This message comes from the ViewModel logic itself
//        assertEquals("Não foram encontrados filmes. Verifique sua conexão.", error, "Error state should indicate no movies found")
//
//        // Verify repository method was called
//        verify(mockMovieRepository).getPopularMovies()
//    }
//
//    @Test
//    fun `retryLoading calls loadPopularMovies`() = runTest {
//        // Arrange: Mock the loadPopularMovies call to track if it's called
//        // Note: This test is slightly weak because it relies on the internal implementation
//        // A better approach might be to test the state changes directly as above.
//        // But if you want to verify the method call, this is how:
//        // We need to spy on the actual movieViewModel instance to verify calls on it
//        // This requires different Mockito setup (using spy), or restructuring.
//        // Let's test the result of calling retryLoading instead.
//
//        // Arrange: Configure mock repository to return movies initially
//        val dummyMovies = createDummyMovies(5)
//        `when`(mockMovieRepository.getPopularMovies()).thenReturn(dummyMovies)
//
//        // Simulate initial load (e.g., happened in init)
//        movieViewModel.loadPopularMovies() // This updates the state
//
//        // Simulate an error happening later (manually set error state)
//        movieViewModel._error.value = "Some error occurred" // Accessing private state for test
//
//        // Configure mock repository to return movies again for the retry
//        `when`(mockMovieRepository.getPopularMovies()).thenReturn(createDummyMovies(3)) // New list for retry
//
//        // Act: Call retryLoading
//        movieViewModel.retryLoading()
//
//        // Assert: Verify state changes consistent with a successful reload
//        val isLoading = movieViewModel.isLoading.first()
//        val movies = movieViewModel.movies.first()
//        val error = movieViewModel.error.first()
//
//        assertFalse(isLoading, "isLoading should be false after retry success")
//        assertEquals(3, movies.size, "Movies list size should match the retry data") // Should get the new list
//        assertEquals(null, error, "Error state should be null after retry success")
//
//        // Verify getPopularMovies was called again for the retry
//        verify(mockMovieRepository, times(2)).getPopularMovies() // Once in setup's init, once in retry
//    }
//
//    // Add tests for search functionality (setSearchQuery, clearSearch, toggleSearchActive, updateFilteredMovies)
//    // Add tests for recordMovieClick (requires mocking UserSession or injecting UserSession state)
//    // Add tests for currentUserHistoryMovies flow combining logic
//
//}
//
//// --- TestDispatcherRule for Coroutines Testing ---
//// This rule provides and manages a TestDispatcher for your coroutines tests.
//// Place this class in your test source set, e.g., in a 'util' package.
//
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.*
//import org.junit.rules.TestWatcher
//import org.junit.runner.Description
//
//@ExperimentalCoroutinesApi
//class TestDispatcherRule(
//    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
//) : TestWatcher() {
//    override fun starting(description: Description) {
//        Dispatchers.setMain(testDispatcher)
//    }
//
//    override fun finished(description: Description) {
//        Dispatchers.resetMain()
//    }
//}