package com.ufscar.ufscartaz.data.network

import com.ufscar.ufscartaz.data.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("language") language: String = "pt-BR"): MovieResponse
}
