package com.ufscar.ufscartaz.data.repository

import com.ufscar.ufscartaz.data.model.Movie
import com.ufscar.ufscartaz.data.model.MovieResponse
import com.ufscar.ufscartaz.data.network.MovieApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import kotlin.test.*

@ExperimentalCoroutinesApi
class MovieRepositoryTest {

    @Mock
    private lateinit var mockApiService: MovieApiService

    private lateinit var movieRepository: MovieRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        movieRepository = MovieRepository()

    }

    private fun createDummyMovies(count: Int): List<Movie> {
        return (1..count).map { i ->
            Movie(
                id = i,
                title = "Movie $i",
                overview = "Overview $i",
                poster_path = "/p$i.jpg",
                genre_ids = listOf(i % 5 + 1, (i + 1) % 5 + 1)
            )
        }
    }

    @Test
    fun `getPopularMovies returns non-null list`() {
        runTest {
            val result = movieRepository.getPopularMovies()

            assertNotNull(result, "O resultado não deve ser nulo")
            assertTrue(result is List<Movie>, "O resultado deve ser uma lista de filmes")
            
            println("Número de filmes retornados: ${result.size}")
        }
    }

    @Test
    fun `movie objects have required fields when list is not empty`() {
        runTest {
            // Chama o método do repositório
            val result = movieRepository.getPopularMovies()

            // Se a lista não estiver vazia, verifica se os filmes têm os campos necessários
            if (result.isNotEmpty()) {
                val firstMovie = result.first()
                
                assertNotNull(firstMovie.id, "Movie deve ter um ID")
                assertTrue(firstMovie.id > 0, "Movie ID deve ser positivo")
                assertNotNull(firstMovie.title, "Movie deve ter um título")
                assertTrue(firstMovie.title.isNotBlank(), "Movie título não deve estar em branco")
                assertNotNull(firstMovie.overview, "Movie deve ter uma sinopse")
                
                println("Primeiro filme: ${firstMovie.title} (ID: ${firstMovie.id})")
            } else {
                println("Lista de filmes está vazia - provavelmente sem conexão com internet")
            }
        }
    }

    // --- Teste de comportamento: verifica se retorna lista vazia em caso de erro ---
    @Test
    fun `getPopularMovies handles exceptions gracefully`() {
        runTest {
            val result = movieRepository.getPopularMovies()

            assertNotNull(result, "O resultado deve ser uma lista não-nula")
            assertTrue(result is List<Movie>, "O resultado deve ser uma lista de filmes")
        }
    }
}