package com.ufscar.ufscartaz.data.network

import com.google.common.truth.Truth.assertThat
import com.ufscar.ufscartaz.data.model.MovieResponse
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MovieApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: MovieApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/")) // base URL do mock
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieApiService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getPopularMovies returns movie list with valid data`() = runBlocking {
        // Dado: resposta mockada da API
        val mockResponse = MockResponse()
        mockResponse.setBody("""
            {
              "results": [
                {
                  "id": 123,
                  "title": "Filme Teste",
                  "overview": "Descrição do filme teste",
                  "poster_path": "/poster.jpg",
                  "genre_ids": [28, 12],
                  "backdrop_path": "/backdrop.jpg",
                  "vote_average": 8.5
                }
              ]
            }
        """.trimIndent())
        mockResponse.setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        // Quando: chamada à API
        val response: MovieResponse = api.getPopularMovies()

        // Então: valida os dados
        assertThat(response.results).isNotEmpty()
        val movie = response.results.first()
        assertThat(movie.title).isEqualTo("Filme Teste")
        assertThat(movie.overview).contains("Descrição")
        assertThat(movie.genre_ids).contains(28)
        assertThat(movie.vote_average).isGreaterThan(0.0)
    }
}
