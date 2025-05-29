package com.ufscar.ufscartaz.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ufscar.ufscartaz.data.local.UserDao
import com.ufscar.ufscartaz.data.model.User
import com.ufscar.ufscartaz.data.remote.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.any


@OptIn(ExperimentalCoroutinesApi::class)
class LoginRegisterTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var userDao: UserDao
    private lateinit var apiService: ApiService
    private lateinit var repo: UserRepository

    @Before
    fun setUp() {
        userDao = mock(UserDao::class.java)
        apiService = mock(ApiService::class.java)

        // Passa null no pexelsApiService, pois não será usado nos testes
        repo = UserRepository(userDao, apiService, pexelsApiService = mock(PexelsApiService::class.java))
    }

    // ---------------- LOGIN ----------------

    @Test
    fun `login retorna Success e salva no DAO quando apiService retorna com sucesso`() {
        runBlocking {
            val loginResp = LoginResponse(1, "João", "a@b.com", "token", avatarId = 0)
            `when`(apiService.login(LoginRequest("a@b.com", "pass"))).thenReturn(loginResp)

            val response = repo.login("a@b.com", "pass")
            assertTrue(response is ApiResponse.Success)
            val user = (response as ApiResponse.Success).data
            assertEquals(1, user.id)
            verify(userDao).insertUser(user)
        }
    }

    @Test
    fun `login busca no DAO quando apiService lança excecao`() = runBlocking {
        `when`(apiService.login(any<LoginRequest>())).thenThrow(RuntimeException("fail"))
        val localUser = User(2, "Ana", "x@y.com", "pwd", null, null)
        `when`(userDao.getUserByEmailAndPassword("x@y.com", "pwd")).thenReturn(localUser)

        val response = repo.login("x@y.com", "pwd")
        assertTrue(response is ApiResponse.Success)
        assertEquals(2, (response as ApiResponse.Success).data.id)
    }


    @Test
    fun `login retorna Error quando API e DAO falham`() = runBlocking {
        `when`(apiService.login(any<LoginRequest>())).thenThrow(RuntimeException("fail"))
        `when`(userDao.getUserByEmailAndPassword(anyString(), anyString())).thenReturn(null)

        val response = repo.login("u", "p")
        assertTrue(response is ApiResponse.Error)
        assertEquals("fail-api", (response as ApiResponse.Error).exception.message)
    }

    // ---------------- REGISTER ----------------

    @Test
    fun `register retorna Error se email ja existe no DAO`() = runBlocking {
        `when`(userDao.emailExists("e@e.com")).thenReturn(true)
        val response = repo.register("Nome", "e@e.com", "senha")
        assertTrue(response is ApiResponse.Error)
        assertEquals("Email already registered", (response as ApiResponse.Error).exception.message)
    }

    @Test
    fun `register insere e retorna Success quando tudo ok`() {
        runBlocking {
            `when`(userDao.emailExists("n@e.com")).thenReturn(false)
            val regResp = RegisterResponse(3, "N", "n@e.com", "token")
            `when`(apiService.register(RegisterRequest("N", "n@e.com", "p"))).thenReturn(regResp)

            val response = repo.register("N", "n@e.com", "p")
            assertTrue(response is ApiResponse.Success)
            val user = (response as ApiResponse.Success).data
            assertEquals(3, user.id)
            verify(userDao).insertUser(user)
        }
    }
}
