package com.ufscar.ufscartaz.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API service interface
 */
interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse
} 