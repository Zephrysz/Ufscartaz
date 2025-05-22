package com.ufscar.ufscartaz.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ufscar.ufscartaz.data.model.Movie
import com.ufscar.ufscartaz.data.repository.MovieRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
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
} 