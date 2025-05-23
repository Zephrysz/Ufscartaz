package com.ufscar.ufscartaz.data.repository

import com.ufscar.ufscartaz.data.UserSession
import com.ufscar.ufscartaz.data.local.UserDao
import com.ufscar.ufscartaz.data.model.User
import com.ufscar.ufscartaz.data.remote.*
import com.ufscar.ufscartaz.data.remote.Avatar
import com.ufscar.ufscartaz.data.remote.PexelsApiService
//import com.ufscar.ufscartaz.di.NetworkModule

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.lang.Exception

/**
 * Repository that handles user-related operations (login, registration, etc.)
 */
class UserRepository(
    private val userDao: UserDao,
    private val apiService: ApiService,
    private val pexelsApiService: PexelsApiService
) {
    /**
     * Login a user with email and password
     */
    suspend fun login(email: String, password: String): ApiResponse<User> = withContext(Dispatchers.IO) {
        try {
            try {
                val response = apiService.login(LoginRequest(email, password))

                val user = User(
                    id = response.userId,
                    name = response.name,
                    email = response.email,
                    password = password,
                    avatarPexelsId = null,
                    avatarUrl = null
                )
                userDao.insertUser(user)

                return@withContext ApiResponse.Success(user)
            } catch (e: Exception) {
                val localUser = userDao.getUserByEmailAndPassword(email, password)
                if (localUser != null) {
                    return@withContext ApiResponse.Success(localUser)
                } else {
                    throw e
                }
            }
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
                password = password,
                avatarPexelsId = null,
                avatarUrl = null
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
    suspend fun updateAvatar(userId: Long, avatarPexelsId: Int?, avatarUrl: String?): ApiResponse<Unit> {
        return try {
            userDao.updateUserAvatar(userId, avatarPexelsId, avatarUrl)
            ApiResponse.Success(Unit) // Indicate success
        } catch (e: Exception) {
            ApiResponse.Error(e) // Indicate error
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

    /**
     * Fetches avatars from Pexels API.
     */
    suspend fun fetchAvatars(): ApiResponse<List<Avatar>> = withContext(Dispatchers.IO) {
        val query = "face" // Example search query
        val perPage = 20 // Number of avatars to fetch

        return@withContext try {
            // Perform the API call
            val response = pexelsApiService.searchPhotos(query = query, perPage = perPage, orientation = "square")

            if (response.isSuccessful && response.body() != null) {
                // Map Pexels Photo objects from the successful response body
                val pexelPhotos = response.body()!!.photos // Access the photos list here
                val avatars = pexelPhotos.map { photo ->
                    // Use the 'medium' size for the avatar URL
                    Avatar(pexelsId = photo.id, url = photo.src.medium)
                }
                ApiResponse.Success(avatars)
            } else {
                // Handle non-successful responses (e.g., API key invalid, rate limit)
                val errorBody = response.errorBody()?.string()
                val errorMessage = "API Error: ${response.code()} - ${response.message()} ${errorBody ?: ""}"
                ApiResponse.Error(Exception(errorMessage))
            }
        } catch (e: Exception) {
            // Handle network errors or other exceptions during the call
            ApiResponse.Error(e)
        }
    }


    // You'll need a helper function like this if you don't have one
    // This is a simplified version, adapt from your existing code
//    private inline fun <T> handleApiCall(call: () -> T): ApiResponse<T> {
//        return try {
//            ApiResponse.Success(call.invoke())
//        } catch (e: Exception) {
//            // Log the error or handle specific network exceptions
//            ApiResponse.Error(e)
//        }
//    }
} 