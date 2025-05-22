package com.ufscar.ufscartaz.data.remote

/**
 * A generic class that contains data and status about loading this data.
 */
sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class Error(val exception: Exception) : ApiResponse<Nothing>()
}

/**
 * Response model for login
 */
data class LoginResponse(
    val userId: Long,
    val name: String,
    val email: String,
    val token: String,
    val avatarId: Int
)

/**
 * Response model for registration
 */
data class RegisterResponse(
    val userId: Long,
    val name: String,
    val email: String,
    val token: String
)

/**
 * Request model for login
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Request model for registration
 */
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
) 