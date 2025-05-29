package com.ufscar.ufscartaz.data.remote

sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class Error(val exception: Exception) : ApiResponse<Nothing>()
}

data class LoginResponse(
    val userId: Long,
    val name: String,
    val email: String,
    val token: String,
    val avatarId: Int
)

data class RegisterResponse(
    val userId: Long,
    val name: String,
    val email: String,
    val token: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
) 