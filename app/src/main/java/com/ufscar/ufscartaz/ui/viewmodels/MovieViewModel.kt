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
        val appDatabase = AppDatabase.getDatabase(application)
        val movieHistoryDao = appDatabase.movieHistoryDao() // Get the new DAO
        movieHistoryRepository = MovieHistoryRepository(movieHistoryDao) // Initialize the repository

        loadPopularMovies()
        setupSearchDebounce()
    }
    
    @OptIn(FlowPreview::class)
    private fun setupSearchDebounce() {
        viewModelScope.launch {
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
        
        if (query.isEmpty()) {
            _filteredMovies.value = emptyList()
        }
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
        
        val exactMatches = _movies.value.filter { movie ->
            movie.title.lowercase() == query
        }
        
        val titleMatches = _movies.value.filter { movie ->
            movie.title.lowercase().contains(query) && !exactMatches.contains(movie)
        }
        
        val descriptionMatches = _movies.value.filter { movie ->
            movie.overview.lowercase().contains(query) && 
            !exactMatches.contains(movie) && 
            !titleMatches.contains(movie)
        }
        
        _filteredMovies.value = exactMatches + titleMatches + descriptionMatches
    }
    
    fun getMoviesByGenre(genreId: Int): List<Movie> {
        return _movies.value.filter { movie ->
            movie.genre_ids.contains(genreId)
        }
    }

    fun recordMovieClick(movieId: Int) {
        val userId = UserSession.currentUser.value?.id

        if (userId != null) {
            viewModelScope.launch {
                try {
                    movieHistoryRepository.addMovieToHistory(userId, movieId)
                    Log.d("MovieViewModel", "Recorded click for movie ID $movieId for user ID $userId")
                } catch (e: Exception) {
                    Log.e("MovieViewModel", "Error recording movie click history", e)
                }
            }
        } else {
            Log.w("MovieViewModel", "Attempted to record movie click without a logged-in user.")
        }
    }

    val currentUserHistoryMovies: StateFlow<List<Movie>> =
        UserSession.currentUser
            .flatMapLatest { user ->
                if (user != null) {
                    combine(
                        movieHistoryRepository.getUserHistory(user.id), // Flow<List<MovieHistoryEntry>>
                        _movies // Flow<List<Movie>>
                    ) { historyEntries, allMovies ->
                        Log.d("MovieViewModel", "Combining history (${historyEntries.size} entries) with ${allMovies.size} movies")
                        historyEntries
                            .distinctBy { it.movieId }
                            .take(15) // Display the last 15 unique movies
                            .mapNotNull { historyEntry ->
                                allMovies.find { it.id == historyEntry.movieId }
                            }
                    }
                } else {
                    emptyFlow()
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000), // Keep collecting for a bit after observers disappear
                initialValue = emptyList() // Initial value when no data is available yet
            )
}

