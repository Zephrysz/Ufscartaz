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


class UserRepository(
    private val userDao: UserDao,
    private val apiService: ApiService,
    private val pexelsApiService: PexelsApiService
) {

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

    suspend fun updateAvatar(userId: Long, avatarPexelsId: Int?, avatarUrl: String?): ApiResponse<Unit> {
        return try {
            userDao.updateUserAvatar(userId, avatarPexelsId, avatarUrl)
            ApiResponse.Success(Unit) // Indicate success
        } catch (e: Exception) {
            ApiResponse.Error(e) // Indicate error
        }
    }
    

    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers()
    }

    suspend fun getUserByEmail(email: String): User? = withContext(Dispatchers.IO) {
        return@withContext userDao.getUserByEmail(email)
    }

    suspend fun fetchAvatars(query: String, perPage: Int = 20): ApiResponse<List<Avatar>> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Perform the API call with the provided query
            val response = pexelsApiService.searchPhotos(
                query = query,
                perPage = perPage,
                orientation = "square" // Can keep or make this a parameter too
            )

            if (response.isSuccessful && response.body() != null) {
                val pexelPhotos = response.body()!!.photos
                val avatars = pexelPhotos.map { photo ->
                    Avatar(pexelsId = photo.id, url = photo.src.medium)
                }
                ApiResponse.Success(avatars)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = "API Error for query '$query': ${response.code()} - ${response.message()} ${errorBody ?: ""}"
                ApiResponse.Error(Exception(errorMessage))
            }
        } catch (e: Exception) {
            ApiResponse.Error(e) // Handle network errors or other exceptions
        }
    }
} 