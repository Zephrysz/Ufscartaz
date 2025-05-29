//package com.ufscar.ufscartaz.data.repository
//
//import com.ufscar.ufscartaz.data.model.Movie
//import com.ufscar.ufscartaz.data.model.MovieResponse
//import com.ufscar.ufscartaz.data.network.MovieApiService // Import MovieApiService
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.runTest // Import runTest
//import okhttp3.ResponseBody.Companion.toResponseBody
//import org.junit.Before // Import Before
//import org.junit.Test // Import Test
//import org.mockito.Mock // Import Mock
//import org.mockito.Mockito.* // Import Mockito functions (when, verify, etc.)
//import org.mockito.MockitoAnnotations // Import MockitoAnnotations
//import retrofit2.HttpException
//import retrofit2.Response
//import java.io.IOException
//import kotlin.test.* // Import all kotlin.test functions (assertEquals, assertTrue, etc.)
//
//@ExperimentalCoroutinesApi
//class MovieRepositoryTest {
//
//    // Mocks necessários
//    @Mock
//    private lateinit var mockApiService: MovieApiService // Mock do serviço API
//
//    // A instância do repositório que será testada
//    private lateinit var movieRepository: MovieRepository
//
//    // Configuração antes de cada teste
//    @Before
//    fun setup() {
//        // Inicializa os mocks anotados com @Mock
//        MockitoAnnotations.openMocks(this)
//        // Instancia o repositório INJETANDO o mock do serviço API
//        movieRepository = MovieRepository(mockApiService) // <--- MUDANÇA AQUI: INJETANDO O MOCK
//    }
//
//    // --- Helper function para criar filmes fictícios ---
//    private fun createDummyMovies(count: Int): List<Movie> {
//        return (1..count).map { i ->
//            Movie(
//                id = i,
//                title = "Movie $i",
//                overview = "Overview $i",
//                poster_path = "/p$i.jpg",
//                genre_ids = listOf(i % 5 + 1, (i + 1) % 5 + 1) // IDs de gênero fictícios > 0
//            )
//        }
//    }
//
//    // --- Teste de sucesso geral para getPopularMovies ---
//    @Test
//    fun `getPopularMovies success returns list of movies`() = runTest {
//        // Organizar (Arrange): Crie dados fictícios para a resposta da API
//        val dummyMovies = createDummyMovies(2) // Uma lista pequena de filmes fictícios
//        val dummyResponse = MovieResponse(results = dummyMovies)
//
//        // Organizar (Arrange): Configure o mock do serviço API para retornar a resposta fictícia
//        // quando o método getPopularMovies for chamado com qualquer string de idioma
//        `when`(mockApiService.getPopularMovies(anyString())).thenReturn(dummyResponse)
//
//        // Agir (Act): Chame o método do repositório que você quer testar
//        val result = movieRepository.getPopularMovies() // Usa o idioma padrão ("pt-BR")
//
//        // Afirmar (Assert): Verifique se o resultado é a lista esperada de filmes
//        assertNotNull(result, "O resultado não deve ser nulo")
//        assertEquals(dummyMovies.size, result.size, "Deve retornar o mesmo número de filmes que os dados fictícios")
//        assertEquals(dummyMovies[0].title, result[0].title, "O título do primeiro filme deve ser igual")
//        assertEquals(dummyMovies[1].id, result[1].id, "O ID do segundo filme deve ser igual")
//
//        // Opcional: Verifique se o método do serviço API foi chamado exatamente uma vez com o idioma esperado
//        verify(mockApiService).getPopularMovies("pt-BR") // Verifica se foi chamado com "pt-BR"
//        verifyNoMoreInteractions(mockApiService) // Garante que nenhum outro método do mock foi chamado
//    }
//
//
//    // --- Teste para um cenário específico: verificar se um filme conhecido (como Minecraft) é retornado ---
//    @Test
//    fun `getPopularMovies includes specific movie when present in API response`() = runTest {
//        // Organizar (Arrange): Crie um filme fictício específico (simulando o "Minecraft")
//        val minecraftMovie = Movie(
//            id = 950387, // ID conhecido (exemplo do logcat)
//            title = "Um Filme Minecraft",
//            overview = "Uma aventura no mundo de blocos...",
//            poster_path = "/minecraft_poster.jpg",
//            genre_ids = listOf(12, 14, 16), // Aventura, Fantasia, Animação
//            backdrop_path = "/minecraft_backdrop.jpg",
//            vote_average = 7.8
//        )
//        // Crie outros filmes fictícios e inclua o filme do Minecraft na lista de resposta
//        val dummyMovies = listOf(
//            createDummyMovies(1)[0], // Um filme genérico
//            minecraftMovie,          // O filme do Minecraft
//            createDummyMovies(1)[0].copy(id = 3) // Outro filme genérico com ID diferente
//        )
//        val dummyResponse = MovieResponse(results = dummyMovies)
//
//        // Organizar (Arrange): Configure o mock do serviço API para retornar esta lista específica
//        `when`(mockApiService.getPopularMovies(anyString())).thenReturn(dummyResponse)
//
//        // Agir (Act): Chame o método do repositório
//        val result = movieRepository.getPopularMovies()
//
//        // Afirmar (Assert): Verifique se a lista retornada contém o filme do Minecraft
//        assertNotNull(result, "O resultado não deve ser nulo")
//        assertTrue(result.isNotEmpty(), "A lista de resultados não deve estar vazia")
//
//        // Encontre o filme do Minecraft na lista retornada pelo repositório
//        val foundMinecraftMovie = result.find { it.id == minecraftMovie.id }
//
//        // Afirmar (Assert): Verifique se o filme do Minecraft foi encontrado na lista
//        assertNotNull(foundMinecraftMovie, "O filme do Minecraft deve ser encontrado na lista de resultados")
//
//        // Afirmar (Assert): Verifique algumas informações específicas do filme encontrado
//        assertEquals(minecraftMovie.title, foundMinecraftMovie.title, "O título do filme do Minecraft deve ser igual")
//        assertEquals(minecraftMovie.overview, foundMinecraftMovie.overview, "A sinopse do filme do Minecraft deve ser igual")
//        // Você pode verificar outros campos conforme necessário
//        assertEquals(minecraftMovie.poster_path, foundMinecraftMovie.poster_path)
//        assertEquals(minecraftMovie.genre_ids, foundMinecraftMovie.genre_ids)
//
//        // Opcional: Verifique se o método do serviço API foi chamado
//        verify(mockApiService).getPopularMovies(anyString())
//        verifyNoMoreInteractions(mockApiService)
//    }
//
//
//    // --- Teste de cenário: API retorna lista vazia ---
//    @Test
//    fun `getPopularMovies returns empty list when API response results is empty`() = runTest {
//        // Organizar (Arrange): Crie uma resposta da API com uma lista de resultados vazia
//        val dummyResponse = MovieResponse(results = emptyList())
//
//        // Organizar (Arrange): Configure o mock do serviço API para retornar a resposta vazia
//        `when`(mockApiService.getPopularMovies(anyString())).thenReturn(dummyResponse)
//
//        // Agir (Act): Chame o método do repositório
//        val result = movieRepository.getPopularMovies()
//
//        // Afirmar (Assert): Verifique se o resultado é uma lista vazia
//        assertNotNull(result, "O resultado não deve ser nulo")
//        assertTrue(result.isEmpty(), "A lista de resultados deve estar vazia")
//
//        verify(mockApiService).getPopularMovies(anyString())
//        verifyNoMoreInteractions(mockApiService)
//    }
//
//    // --- Teste de cenário de erro: exceção de IO (ex: sem internet) ---
//    @Test
//    fun `getPopularMovies throws IOException on network error`() = runTest { // Renomeado para ser mais específico
//        // Organizar (Arrange): Crie a exceção esperada
//        val expectedException = IOException("Network error")
//
//        // Organizar (Arrange): Configure o mock do serviço API para lançar a exceção
//        // Use doThrow().`when`() para exceptions que não são declaradas no 'throws' da assinatura
//        doThrow(expectedException).`when`(mockApiService).getPopularMovies(anyString())
//
//        // Agir e Afirmar (Act & Assert): Verifique se a chamada do repositório lança a exceção esperada
//        val caughtException = assertFailsWith<IOException>( // Espera-se uma IOException
//            "MovieRepository deve relançar a IOException em erro de rede"
//        ) {
//            movieRepository.getPopularMovies() // A chamada que deve lançar a exceção
//        }
//
//        // Afirmar (Assert): Verifique a mensagem da exceção (opcional, mas bom)
//        assertEquals(expectedException.message, caughtException.message)
//
//        verify(mockApiService).getPopularMovies(anyString())
//        verifyNoMoreInteractions(mockApiService)
//    }
//
//    // --- Teste de cenário de erro: exceção HTTP (ex: 401, 404) ---
//    @Test
//    fun `getPopularMovies throws HttpException on non-success HTTP response`() = runTest {
//        // Organizar (Arrange): Crie uma resposta de erro HTTP fictícia
//        val errorResponse = Response.error<MovieResponse>(
//            401, // Código de status HTTP (Ex: Não Autorizado)
//            "Unauthorized".toResponseBody(null) // Corpo do erro (pode ser qualquer coisa)
//        )
//
//        // Organizar (Arrange): Retrofit lança HttpException para respostas não-sucesso
//        val expectedException = HttpException(errorResponse)
//        // Use doThrow().`when`() para lançar a HttpException
//        doThrow(expectedException).`when`(mockApiService).getPopularMovies(anyString())
//
//        // Agir e Afirmar (Act & Assert): Verifique se a chamada do repositório lança a HttpException esperada
//        val caughtException = assertFailsWith<HttpException>( // Espera-se uma HttpException
//            "MovieRepository deve relançar a HttpException em resposta HTTP não-sucesso"
//        ) {
//            movieRepository.getPopularMovies() // A chamada que deve lançar a exceção
//        }
//
//        // Afirmar (Assert): Verifique o código HTTP da exceção
//        assertEquals(401, caughtException.code())
//
//        verify(mockApiService).getPopularMovies(anyString())
//        verifyNoMoreInteractions(mockApiService)
//    }
//
//    // Adicione mais testes para diferentes cenários de erro ou outros métodos do repositório se houverem
//}