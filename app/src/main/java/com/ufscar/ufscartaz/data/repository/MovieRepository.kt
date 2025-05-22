package com.ufscar.ufscartaz.data.repository

import com.ufscar.ufscartaz.data.model.Movie
import com.ufscar.ufscartaz.data.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepository {
    private val api = RetrofitInstance.api
    
    suspend fun getPopularMovies(): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                api.getPopularMovies().results
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
