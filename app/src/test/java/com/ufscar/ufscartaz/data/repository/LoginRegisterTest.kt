package com.ufscar.ufscartaz.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ufscar.ufscartaz.data.local.UserDao
import com.ufscar.ufscartaz.data.model.User
import com.ufscar.ufscartaz.data.remote.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import org.mockito.kotlin.*


@OptIn(ExperimentalCoroutinesApi::class)
class LoginRegisterTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var userDao: UserDao
    private lateinit var apiService: ApiService
    private lateinit var pexelsApiService: PexelsApiService
    private lateinit var repo: UserRepository

    @Before
    fun setUp() {
        userDao = mock<UserDao>()
        apiService = mock<ApiService>()
        pexelsApiService = mock<PexelsApiService>()

        repo = UserRepository(userDao, apiService, pexelsApiService)
    }

    // ---------------- LOGIN ----------------

    @Test
    fun `login retorna Success e salva no DAO quando apiService retorna com sucesso`() {
        runBlocking {
            val loginResp = LoginResponse(1, "João", "a@b.com", "token", avatarId = 0)
            whenever(apiService.login(LoginRequest("a@b.com", "pass"))).thenReturn(loginResp)

            val response = repo.login("a@b.com", "pass")
            assertTrue(response is ApiResponse.Success)
            val user = (response as ApiResponse.Success).data
            assertEquals(1, user.id)
            assertEquals("João", user.name)
            assertEquals("a@b.com", user.email)
            verify(userDao).insertUser(user)
        }
    }

    @Test
    fun `login busca no DAO quando apiService lança excecao`() {
        runBlocking {
            whenever(apiService.login(any<LoginRequest>())).thenThrow(RuntimeException("fail"))
            val localUser = User(2, "Ana", "x@y.com", "pwd", null, null)
            whenever(userDao.getUserByEmailAndPassword("x@y.com", "pwd")).thenReturn(localUser)

            val response = repo.login("x@y.com", "pwd")
            assertTrue(response is ApiResponse.Success)
            assertEquals(2, (response as ApiResponse.Success).data.id)
            assertEquals("Ana", response.data.name)
        }
    }

    @Test
    fun `login retorna Error quando API e DAO falham`() {
        runBlocking {
            whenever(apiService.login(any<LoginRequest>())).thenThrow(RuntimeException("Network error"))
            whenever(userDao.getUserByEmailAndPassword(any(), any())).thenReturn(null)

            val response = repo.login("u", "p")
            assertTrue(response is ApiResponse.Error)
            assertNotNull((response as ApiResponse.Error).exception)
        }
    }

    // ---------------- REGISTER ----------------

    @Test
    fun `register retorna Error se email ja existe no DAO`() {
        runBlocking {
            whenever(userDao.emailExists("e@e.com")).thenReturn(true)
            val response = repo.register("Nome", "e@e.com", "senha")
            assertTrue(response is ApiResponse.Error)
            assertEquals("Email already registered", (response as ApiResponse.Error).exception.message)
            
            // Verify that API was never called
            verify(apiService, never()).register(any<RegisterRequest>())
        }
    }

    @Test
    fun `register insere e retorna Success quando tudo ok`() {
        runBlocking {
            whenever(userDao.emailExists("n@e.com")).thenReturn(false)
            val regResp = RegisterResponse(3, "N", "n@e.com", "token")
            whenever(apiService.register(RegisterRequest("N", "n@e.com", "p"))).thenReturn(regResp)

            val response = repo.register("N", "n@e.com", "p")
            assertTrue(response is ApiResponse.Success)
            val user = (response as ApiResponse.Success).data
            assertEquals(3, user.id)
            assertEquals("N", user.name)
            assertEquals("n@e.com", user.email)
            verify(userDao).insertUser(user)
        }
    }

    @Test
    fun `register retorna Error quando API falha`() {
        runBlocking {
            whenever(userDao.emailExists("test@test.com")).thenReturn(false)
            whenever(apiService.register(any<RegisterRequest>())).thenThrow(RuntimeException("Server error"))

            val response = repo.register("Test", "test@test.com", "password")
            assertTrue(response is ApiResponse.Error)
            assertNotNull((response as ApiResponse.Error).exception)
        }
    }

    // ---------------- UPDATE AVATAR ----------------

    @Test
    fun `updateAvatar retorna Success quando DAO update funciona`() {
        runBlocking {
            val userId = 1L
            val avatarPexelsId = 123
            val avatarUrl = "https://example.com/avatar.jpg"

            val response = repo.updateAvatar(userId, avatarPexelsId, avatarUrl)
            
            assertTrue(response is ApiResponse.Success)
            verify(userDao).updateUserAvatar(userId, avatarPexelsId, avatarUrl)
        }
    }

    @Test
    fun `updateAvatar retorna Error quando DAO lança exceção`() {
        runBlocking {
            val userId = 1L
            val avatarPexelsId = 123
            val avatarUrl = "https://example.com/avatar.jpg"

            whenever(userDao.updateUserAvatar(userId, avatarPexelsId, avatarUrl))
                .thenThrow(RuntimeException("Database error"))

            val response = repo.updateAvatar(userId, avatarPexelsId, avatarUrl)
            
            assertTrue(response is ApiResponse.Error)
            assertEquals("Database error", (response as ApiResponse.Error).exception.message)
        }
    }

    // ---------------- GET USER BY EMAIL ----------------

    @Test
    fun `getUserByEmail retorna user quando existe`() {
        runBlocking {
            val email = "test@example.com"
            val user = User(1, "Test User", email, "password", null, null)
            
            whenever(userDao.getUserByEmail(email)).thenReturn(user)

            val result = repo.getUserByEmail(email)
            
            assertEquals(user, result)
            verify(userDao).getUserByEmail(email)
        }
    }

    @Test
    fun `getUserByEmail retorna null quando não existe`() {
        runBlocking {
            val email = "nonexistent@example.com"
            
            whenever(userDao.getUserByEmail(email)).thenReturn(null)

            val result = repo.getUserByEmail(email)
            
            assertNull(result)
            verify(userDao).getUserByEmail(email)
        }
    }
}
