package com.ufscar.ufscartaz.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ufscar.ufscartaz.data.local.UserDao
import com.ufscar.ufscartaz.data.model.User
import com.ufscar.ufscartaz.data.remote.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var userDao: UserDao
    private lateinit var apiService: ApiService
    private lateinit var pexelsApiService: PexelsApiService
    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        userDao = mock<UserDao>()
        apiService = mock<ApiService>()
        pexelsApiService = mock<PexelsApiService>()
        userRepository = UserRepository(userDao, apiService, pexelsApiService)
    }

    @Test
    fun `login success via API`() {
        runBlocking {
            // Given
            val email = "test@example.com"
            val password = "password"
            val loginRequest = LoginRequest(email, password)
            val loginResponse = LoginResponse(1L, "Test User", email, "token", 0)

            whenever(apiService.login(loginRequest)).thenReturn(loginResponse)

            // When
            val result = userRepository.login(email, password)

            // Then
            assertTrue(result is ApiResponse.Success)
            assertEquals(loginResponse.userId, (result as ApiResponse.Success<User>).data.id)
            assertEquals(loginResponse.name, result.data.name)
            assertEquals(loginResponse.email, result.data.email)
            
            // Verify that user was saved to DAO
            verify(userDao).insertUser(any<User>())
        }
    }

    @Test
    fun `login fallback to DAO when API fails`() {
        runBlocking {
            // Given
            val email = "test@example.com"
            val password = "password"
            val localUser = User(2L, "Test User", email, password, null, null)

            whenever(apiService.login(any<LoginRequest>())).thenThrow(RuntimeException("Network error"))
            whenever(userDao.getUserByEmailAndPassword(email, password)).thenReturn(localUser)

            // When
            val result = userRepository.login(email, password)

            // Then
            assertTrue(result is ApiResponse.Success)
            assertEquals(localUser.id, (result as ApiResponse.Success<User>).data.id)
            assertEquals(localUser.name, result.data.name)
        }
    }

    @Test
    fun `login fails when both API and DAO fail`() {
        runBlocking {
            // Given
            val email = "test@example.com"
            val password = "password"

            whenever(apiService.login(any<LoginRequest>())).thenThrow(RuntimeException("Network error"))
            whenever(userDao.getUserByEmailAndPassword(email, password)).thenReturn(null)

            // When
            val result = userRepository.login(email, password)

            // Then
            assertTrue(result is ApiResponse.Error)
        }
    }

    @Test
    fun `register success`() {
        runBlocking {
            // Given
            val name = "Test User"
            val email = "test@example.com"
            val password = "password"
            val registerRequest = RegisterRequest(name, email, password)
            val registerResponse = RegisterResponse(1L, name, email, "token")

            whenever(userDao.emailExists(email)).thenReturn(false)
            whenever(apiService.register(registerRequest)).thenReturn(registerResponse)

            // When
            val result = userRepository.register(name, email, password)

            // Then
            assertTrue(result is ApiResponse.Success)
            assertEquals(registerResponse.userId, (result as ApiResponse.Success<User>).data.id)
            assertEquals(registerResponse.name, result.data.name)
            assertEquals(registerResponse.email, result.data.email)
            
            // Verify that user was saved to DAO
            verify(userDao).insertUser(any<User>())
        }
    }

    @Test
    fun `register fails when email exists`() {
        runBlocking {
            // Given
            val name = "Test User"
            val email = "test@example.com"
            val password = "password"

            whenever(userDao.emailExists(email)).thenReturn(true)

            // When
            val result = userRepository.register(name, email, password)

            // Then
            assertTrue(result is ApiResponse.Error)
            assertEquals("Email already registered", (result as ApiResponse.Error).exception.message)
            
            // Verify that API was never called
            verify(apiService, never()).register(any<RegisterRequest>())
        }
    }

    @Test
    fun `updateAvatar success`() {
        runBlocking {
            // Given
            val userId = 1L
            val avatarPexelsId = 123
            val avatarUrl = "https://example.com/avatar.jpg"

            // When
            val result = userRepository.updateAvatar(userId, avatarPexelsId, avatarUrl)

            // Then
            assertTrue(result is ApiResponse.Success)
            verify(userDao).updateUserAvatar(userId, avatarPexelsId, avatarUrl)
        }
    }

    @Test
    fun `getUserByEmail returns user when exists`() {
        runBlocking {
            // Given
            val email = "test@example.com"
            val user = User(1L, "Test User", email, "password", null, null)
            
            whenever(userDao.getUserByEmail(email)).thenReturn(user)

            // When
            val result = userRepository.getUserByEmail(email)

            // Then
            assertEquals(user, result)
        }
    }

    @Test
    fun `getUserByEmail returns null when user does not exist`() {
        runBlocking {
            // Given
            val email = "nonexistent@example.com"
            
            whenever(userDao.getUserByEmail(email)).thenReturn(null)

            // When
            val result = userRepository.getUserByEmail(email)

            // Then
            assertNull(result)
        }
    }
}