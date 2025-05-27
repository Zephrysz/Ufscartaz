package com.ufscar.ufscartaz.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import android.app.Application
import androidx.lifecycle.AndroidViewModel // para ter acesso ao banco de dados, instancia local, contexto, etc

import com.ufscar.ufscartaz.data.UserSession
import com.ufscar.ufscartaz.data.model.User
import com.ufscar.ufscartaz.data.local.AppDatabase
import com.ufscar.ufscartaz.data.model.Movie
import com.ufscar.ufscartaz.data.model.MovieHistoryEntry
import com.ufscar.ufscartaz.data.repository.MovieRepository
import com.ufscar.ufscartaz.data.repository.MovieHistoryRepository
import com.ufscar.ufscartaz.di.NetworkModule

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.util.Log

class MovieViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MovieRepository()

    private val movieHistoryRepository: MovieHistoryRepository

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _filteredMovies = MutableStateFlow<List<Movie>>(emptyList())
    val filteredMovies: StateFlow<List<Movie>> = _filteredMovies.asStateFlow()
    
    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    val currentUser: StateFlow<User?> = UserSession.currentUser

    init {
        // Initialize the new history repository using the database instance from Application context
        val appDatabase = AppDatabase.getDatabase(application)
        val movieHistoryDao = appDatabase.movieHistoryDao() // Get the new DAO
        movieHistoryRepository = MovieHistoryRepository(movieHistoryDao) // Initialize the repository

        loadPopularMovies()
        setupSearchDebounce()
    }
    
    @OptIn(FlowPreview::class)
    private fun setupSearchDebounce() {
        viewModelScope.launch {
            // Debounce a pesquisa para evitar muitas atualizações rápidas
            _searchQuery
                .debounce(300) // 300ms de atraso para melhor desempenho
                .collect { query ->
                    if (query.isNotEmpty()) {
                        updateFilteredMovies()
                    }
                }
        }
    }
    
    fun loadPopularMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val movieList = repository.getPopularMovies()
                if (movieList.isEmpty()) {
                    _error.value = "Não foram encontrados filmes. Verifique sua conexão."
                } else {
                    _movies.value = movieList
                    // Se a pesquisa estiver ativa, também atualiza os filmes filtrados
                    if (_isSearchActive.value) {
                        updateFilteredMovies()
                    }
                }
            } catch (e: Exception) {
                _error.value = "Erro ao carregar filmes: ${e.message ?: "Erro desconhecido"}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun retryLoading() {
        loadPopularMovies()
    }
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        _isSearchActive.value = query.isNotEmpty()
        
        // Se a query estiver vazia, limpa os resultados imediatamente
        if (query.isEmpty()) {
            _filteredMovies.value = emptyList()
        }
        // Caso contrário, o debounce cuidará da atualização
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
        _isSearchActive.value = false
        _filteredMovies.value = emptyList()
    }
    
    fun toggleSearchActive() {
        _isSearchActive.value = !_isSearchActive.value
        if (!_isSearchActive.value) {
            clearSearch()
        }
    }
    
    private fun updateFilteredMovies() {
        val query = _searchQuery.value.trim().lowercase()
        if (query.isEmpty()) {
            _filteredMovies.value = emptyList()
            return
        }
        
        // Pesquisa por título com prioridade (match exato)
        val exactMatches = _movies.value.filter { movie ->
            movie.title.lowercase() == query
        }
        
        // Pesquisa por título com correspondência parcial
        val titleMatches = _movies.value.filter { movie ->
            movie.title.lowercase().contains(query) && !exactMatches.contains(movie)
        }
        
        // Pesquisa por descrição se necessário
        val descriptionMatches = _movies.value.filter { movie ->
            movie.overview.lowercase().contains(query) && 
            !exactMatches.contains(movie) && 
            !titleMatches.contains(movie)
        }
        
        // Combina os resultados com prioridade para títulos
        _filteredMovies.value = exactMatches + titleMatches + descriptionMatches
    }
    
    fun getMoviesByGenre(genreId: Int): List<Movie> {
        return _movies.value.filter { movie ->
            movie.genre_ids.contains(genreId)
        }
    }

    /**
     * Records a movie click in the history for the current user.
     */
    fun recordMovieClick(movieId: Int) {
        // Get the current user's ID from the UserSession singleton
        val userId = UserSession.currentUser.value?.id

        if (userId != null) {
            // Launch a coroutine to perform the database insert
            viewModelScope.launch {
                try {
                    movieHistoryRepository.addMovieToHistory(userId, movieId)
                    Log.d("MovieViewModel", "Recorded click for movie ID $movieId for user ID $userId")
                } catch (e: Exception) {
                    Log.e("MovieViewModel", "Error recording movie click history", e)
                    // Optionally, update an error state or show a Toast
                }
            }
        } else {
            // This case shouldn't happen if the user is required to be logged in
            Log.w("MovieViewModel", "Attempted to record movie click without a logged-in user.")
        }
    }

    /**
     * Exposes the history of movies for the current user as a list of Movie objects.
     * Combines the history entries with the full movie list.
     */
    val currentUserHistoryMovies: StateFlow<List<Movie>> =
        UserSession.currentUser
            .flatMapLatest { user ->
                if (user != null) {
                    // Combine the user's history entries flow with the main movies list flow
                    combine(
                        movieHistoryRepository.getUserHistory(user.id), // Flow<List<MovieHistoryEntry>>
                        _movies // Flow<List<Movie>>
                    ) { historyEntries, allMovies ->
                        // Process the latest history entries and the latest movie list
                        Log.d("MovieViewModel", "Combining history (${historyEntries.size} entries) with ${allMovies.size} movies")
                        historyEntries
                            // Use distinctBy to show each movie only once in history
                            .distinctBy { it.movieId }
                            // Limit to a reasonable number of recent items
                            .take(15) // Display the last 15 unique movies
                            // Map each history entry to its corresponding Movie object
                            .mapNotNull { historyEntry ->
                                // Find the movie in the main list.
                                // This relies on the main list containing the history movies.
                                allMovies.find { it.id == historyEntry.movieId }
                            }
                        // The history entries are already ordered by timestamp DESC by the DAO query
                        // So the resulting list of Movies will also be in recent-first order
                    }
                } else {
                    // If no user is logged in, emit an empty list immediately
                    emptyFlow()
                }
            }
            // Convert the resulting Flow<List<Movie>> into a StateFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000), // Keep collecting for a bit after observers disappear
                initialValue = emptyList() // Initial value when no data is available yet
            )
}

