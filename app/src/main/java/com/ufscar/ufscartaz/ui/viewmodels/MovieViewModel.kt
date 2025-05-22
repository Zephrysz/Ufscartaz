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
} 