package com.ufscar.ufscartaz.di

import com.ufscar.ufscartaz.data.remote.ApiService
import com.ufscar.ufscartaz.data.remote.FakeApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Network dependency injection module
 */
object NetworkModule {
    
    // Base URL for the API (not actually used in the fake implementation)
    private const val BASE_URL = "https://api.ufscartaz.com/"
    
    // Create an HTTP client with logging
    private fun createOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }
    
    // Create a Retrofit instance
    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    // Provide the API service
    fun provideApiService(): ApiService {
        // For now, use the fake implementation instead of the actual Retrofit service
        // In a real app, you would return createRetrofit().create(ApiService::class.java)
        return FakeApiService()
    }
} 