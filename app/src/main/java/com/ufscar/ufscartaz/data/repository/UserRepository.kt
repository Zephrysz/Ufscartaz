package com.ufscar.ufscartaz.data.repository

import com.ufscar.ufscartaz.data.local.UserDao
import com.ufscar.ufscartaz.data.model.User
import com.ufscar.ufscartaz.data.remote.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repository that handles user-related operations (login, registration, etc.)
 */
class UserRepository(
    private val userDao: UserDao,
    private val apiService: ApiService
) {
    /**
     * Login a user with email and password
     */
    suspend fun login(email: String, password: String): ApiResponse<User> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(LoginRequest(email, password))

            val user = User(
                id = response.userId,
                name = response.name,
                email = response.email,
                password = password,
                avatarId = response.avatarId
            )
            userDao.insertUser(user)

            return@withContext ApiResponse.Success(user)
        } catch (e: Exception) {
            return@withContext ApiResponse.Error(e)
        }
    }
    
    /**
     * Register a new user
     */
    suspend fun register(name: String, email: String, password: String): ApiResponse<User> = withContext(Dispatchers.IO) {
        try {
            if (userDao.emailExists(email)) {
                return@withContext ApiResponse.Error(Exception("Email already registered"))
            }

            val response = apiService.register(RegisterRequest(name, email, password))
            
            val user = User(
                id = response.userId,
                name = response.name,
                email = response.email,
                password = password
            )
            userDao.insertUser(user)
            
            return@withContext ApiResponse.Success(user)
        } catch (e: Exception) {
            return@withContext ApiResponse.Error(e)
        }
    }
    
    /**
     * Update user's avatar
     */
    suspend fun updateAvatar(userId: Long, avatarId: Int): ApiResponse<Boolean> = withContext(Dispatchers.IO) {
        try {
            userDao.updateUserAvatar(userId, avatarId)
            return@withContext ApiResponse.Success(true)
        } catch (e: Exception) {
            return@withContext ApiResponse.Error(e)
        }
    }
    
    /**
     * Get all users
     */
    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers()
    }
    
    /**
     * Get user by email
     */
    suspend fun getUserByEmail(email: String): User? = withContext(Dispatchers.IO) {
        return@withContext userDao.getUserByEmail(email)
    }
} 