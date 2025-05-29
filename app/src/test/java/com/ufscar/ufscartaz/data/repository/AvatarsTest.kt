// UserRepositoryTest.kt
package com.ufscar.ufscartaz.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ufscar.ufscartaz.data.local.UserDao
import com.ufscar.ufscartaz.data.model.User
import com.ufscar.ufscartaz.data.remote.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.Assert.*
import org.mockito.Mockito.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalCoroutinesApi::class)
class AvatarsTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockWebServer: MockWebServer
    private lateinit var pexelsService: PexelsApiService
    private lateinit var userDao: UserDao
    private lateinit var apiService: ApiService
    private lateinit var repo: UserRepository

    @Before
    fun setUp() {
        // 1) MockWebServer para Pexels
        mockWebServer = MockWebServer().apply { start() }
        pexelsService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PexelsApiService::class.java)

        // 2) DAO e ApiService “reais” são mockados
        userDao = mock(UserDao::class.java)
        apiService = mock(ApiService::class.java)

        // 3) Repository com dependências
        repo = UserRepository(userDao, apiService, pexelsService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    // -------------- fetchAvatars --------------

    @Test
    fun `fetchAvatars retorna Success com lista de Avatar quando 200 OK`() = runBlocking {
        // prepara JSON de resposta
        val json = """
        {
          "photos":[
            {
              "id": 42,
              "width": 100,"height":100,
              "url":"https://pexels/42",
              "photographer":"Test","photographer_url":"u","photographer_id":1,
              "avgColor":"#000","src":{
                "original":"o","large2x":"l2","large":"l","medium":"m","small":"s",
                "portrait":"p","landscape":"g","tiny":"t"
              },
              "liked":false,"alt":"alt"
            }
          ],
          "total_results":1,"per_page":20,"page":1,"next_page":null
        }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(json)
        )

        val result = repo.fetchAvatars("qualquer", perPage = 1)
        assertTrue(result is ApiResponse.Success)
        val avatars = (result as ApiResponse.Success).data
        assertEquals(1, avatars.size)
        assertEquals(42, avatars.first().pexelsId)
        assertEquals("m", avatars.first().url)
    }

    @Test
    fun `fetchAvatars retorna Error quando 500`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))
        val result = repo.fetchAvatars("erro")
        assertTrue(result is ApiResponse.Error)
        val msg = (result as ApiResponse.Error).exception.message!!
        assertTrue(msg.contains("API Error for query 'erro'"))
    }

    // -------------- login --------------

//    @Test
//    fun `login retorna Success e salva no DAO quando apiService retorna com sucesso`() = runBlocking {
//        // simula login na API
//        val loginResp = LoginResponse(userId = 1, name = "João", email = "a@b.com", token = "t", avatarId = 0)
//        `when`(apiService.login(LoginRequest("a@b.com", "pass")))
//            .thenReturn(loginResp)
//
//        // chama
//        val response = repo.login("a@b.com", "pass")
//        assertTrue(response is ApiResponse.Success)
//        val user = (response as ApiResponse.Success).data
//        assertEquals(1, user.id)
//        // verifica que inseriu no DAO
//        verify(userDao).insertUser(user)
//    }
//
//    @Test
//    fun `login busca no DAO quando apiService lança excecao`() = runBlocking {
//        // API falha
//        `when`(apiService.login(any()))
//            .thenThrow(RuntimeException("fail"))
//        // DAO tem um user
//        val local = User(2, "Ana", "x@y.com", "pwd", null, null)
//        `when`(userDao.getUserByEmailAndPassword("x@y.com", "pwd"))
//            .thenReturn(local)
//
//        val response = repo.login("x@y.com", "pwd")
//        assertTrue(response is ApiResponse.Success)
//        assertEquals(2, (response as ApiResponse.Success).data.id)
//    }
//
//    @Test
//    fun `login retorna Error quando API e DAO falham`() = runBlocking {
//        `when`(apiService.login(any()))
//            .thenThrow(RuntimeException("fail-api"))
//        `when`(userDao.getUserByEmailAndPassword(anyString(), anyString()))
//            .thenReturn(null)
//
//        val response = repo.login("u", "p")
//        assertTrue(response is ApiResponse.Error)
//        assertEquals("fail-api", (response as ApiResponse.Error).exception.message)
//    }
//
//    // -------------- register --------------
//
//    @Test
//    fun `register retorna Error se email ja existe no DAO`() = runBlocking {
//        `when`(userDao.emailExists("e@e.com")).thenReturn(true)
//        val response = repo.register("N", "e@e.com", "p")
//        assertTrue(response is ApiResponse.Error)
//        assertEquals("Email already registered", (response as ApiResponse.Error).exception.message)
//    }
//
//    @Test
//    fun `register insere e retorna Success quando tudo ok`() = runBlocking {
//        `when`(userDao.emailExists("n@e.com")).thenReturn(false)
//        val regResp = RegisterResponse(userId = 3, name = "N", email = "n@e.com", token = "t")
//        `when`(apiService.register(RegisterRequest("N", "n@e.com", "p")))
//            .thenReturn(regResp)
//
//        val response = repo.register("N", "n@e.com", "p")
//        assertTrue(response is ApiResponse.Success)
//        val u = (response as ApiResponse.Success).data
//        assertEquals(3, u.id)
//        verify(userDao).insertUser(u)
//    }

    // -------------- updateAvatar --------------

    @Test
    fun `updateAvatar retorna Success e chama DAO`() = runBlocking {
        // nenhuma exceção => sucesso
        val res = repo.updateAvatar(5, 99, "url")
        assertTrue(res is ApiResponse.Success)
        verify(userDao).updateUserAvatar(5, 99, "url")
    }

    @Test
    fun `updateAvatar retorna Error em excecao do DAO`() = runBlocking {
        doThrow(RuntimeException("db error"))
            .`when`(userDao).updateUserAvatar(anyLong(),  any<Int?>(), any<String?>())

        val res = repo.updateAvatar(1, null, null)
        assertTrue(res is ApiResponse.Error)
        assertEquals("db error", (res as ApiResponse.Error).exception.message)
    }
}
