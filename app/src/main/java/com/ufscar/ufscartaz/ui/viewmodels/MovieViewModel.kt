package com.ufscar.ufscartaz.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ufscar.ufscartaz.data.model.Movie
import com.ufscar.ufscartaz.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {
    private val repository = MovieRepository()
    
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
    
    init {
        loadPopularMovies()
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
        updateFilteredMovies()
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
        _isSearchActive.value = false
        _filteredMovies.value = emptyList()
    }
    
    private fun updateFilteredMovies() {
        val query = _searchQuery.value.trim().lowercase()
        if (query.isEmpty()) {
            _filteredMovies.value = emptyList()
            return
        }
        
        _filteredMovies.value = _movies.value.filter { movie ->
            movie.title.lowercase().contains(query) || 
            movie.overview.lowercase().contains(query)
        }
    }
    
    fun getMoviesByGenre(genreId: Int): List<Movie> {
        return _movies.value.filter { movie ->
            movie.genre_ids.contains(genreId)
        }
    }
} 