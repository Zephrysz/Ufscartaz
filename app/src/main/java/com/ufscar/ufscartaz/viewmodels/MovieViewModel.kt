package com.ufscar.ufscartaz.viewmodels
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.ufscar.ufscartaz.data.model.Movie
//import com.ufscar.ufscartaz.data.repository.MovieRepository
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
//class MovieViewModel : ViewModel() {
//    private val repository = MovieRepository()
//
//    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
//    val movies: StateFlow<List<Movie>> = _movies
//
//    init {
//        fetchMovies()
//    }
//
//    private fun fetchMovies() {
//        viewModelScope.launch {
//            try {
//                val response = repository.getPopularMovies()
//                _movies.value = response.results
//            } catch (e: Exception) {
//                // tratamento de erro (pode usar log, snackbar etc)
//            }
//        }
//    }
//}
