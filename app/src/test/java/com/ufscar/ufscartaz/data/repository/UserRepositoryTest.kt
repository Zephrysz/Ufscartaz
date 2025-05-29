//package com.ufscar.ufscartaz.data.repository
//
//import com.ufscar.ufscartaz.data.local.UserDao
//import com.ufscar.ufscartaz.data.model.User
//import com.ufscar.ufscartaz.data.remote.ApiResponse
//import com.ufscar.ufscartaz.data.remote.ApiService
//import com.ufscar.ufscartaz.data.remote.LoginRequest
//import com.ufscar.ufscartaz.data.remote.LoginResponse
//import com.ufscar.ufscartaz.data.remote.RegisterRequest
//import com.ufscar.ufscartaz.data.remote.RegisterResponse
//import kotlinx.coroutines.runBlocking
//import org.junit.Assert.assertEquals
//import org.junit.Assert.assertTrue
//import org.junit.Before
//import org.junit.Test
//import org.mockito.Mock
//import org.mockito.Mockito.`when`
//import org.mockito.MockitoAnnotations
//
//class UserRepositoryTest {
//
//    @Mock
//    private lateinit var userDao: UserDao
//
//    @Mock
//    private lateinit var apiService: ApiService
//
//    private lateinit var userRepository: UserRepository
//
//    @Before
//    fun setup() {
//        MockitoAnnotations.openMocks(this)
//        userRepository = UserRepository(userDao, apiService)
//    }
//
//    @Test
//    fun `login success via API`() = runBlocking {
//        // Given
//        val email = "test@example.com"
//        val password = "password"
//        val loginRequest = LoginRequest(email, password)
//        val loginResponse = LoginResponse(1L, "Test User", email, "token", 0)
//
//        `when`(apiService.login(loginRequest)).thenReturn(loginResponse)
//
//        // When
//        val result = userRepository.login(email, password)
//
//        // Then
//        assertTrue(result is ApiResponse.Success)
//        assertEquals(loginResponse.userId, (result as ApiResponse.Success<User>).data.id)
//        assertEquals(loginResponse.name, result.data.name)
//        assertEquals(loginResponse.email, result.data.email)
//    }
//
//    @Test
//    fun `register success`() = runBlocking {
//        // Given
//        val name = "Test User"
//        val email = "test@example.com"
//        val password = "password"
//        val registerRequest = RegisterRequest(name, email, password)
//        val registerResponse = RegisterResponse(1L, name, email, "token")
//
//        `when`(userDao.emailExists(email)).thenReturn(false)
//        `when`(apiService.register(registerRequest)).thenReturn(registerResponse)
//
//        // When
//        val result = userRepository.register(name, email, password)
//
//        // Then
//        assertTrue(result is ApiResponse.Success)
//        assertEquals(registerResponse.userId, (result as ApiResponse.Success<User>).data.id)
//        assertEquals(registerResponse.name, result.data.name)
//        assertEquals(registerResponse.email, result.data.email)
//    }
//
//    @Test
//    fun `register fails when email exists`() = runBlocking {
//        // Given
//        val name = "Test User"
//        val email = "test@example.com"
//        val password = "password"
//
//        `when`(userDao.emailExists(email)).thenReturn(true)
//
//        // When
//        val result = userRepository.register(name, email, password)
//
//        // Then
//        assertTrue(result is ApiResponse.Error)
//        assertEquals("Email already registered", (result as ApiResponse.Error).exception.message)
//    }
//}