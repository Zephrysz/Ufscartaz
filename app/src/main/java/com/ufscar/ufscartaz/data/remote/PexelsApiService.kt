package com.ufscar.ufscartaz.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PexelsApiService {

    @GET("search")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 15,
        @Query("orientation") orientation: String = "square"
    ): Response<PexelsSearchResponse>
}