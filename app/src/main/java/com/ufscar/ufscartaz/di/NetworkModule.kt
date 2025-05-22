package com.ufscar.ufscartaz.di

import com.ufscar.ufscartaz.BuildConfig

import com.ufscar.ufscartaz.data.remote.PexelsApiService
import com.ufscar.ufscartaz.data.remote.ApiService
import com.ufscar.ufscartaz.data.remote.FakeApiService

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Network dependency injection module
 */
object NetworkModule {
    
    // Base URL for the API (not actually used in the fake implementation)
    private const val BASE_URL = "https://api.ufscartaz.com/"

    private const val PEXELS_BASE_URL = "https://api.pexels.com/v1/"

    // OkHttpClient with logging and Pexels Authorization interceptor
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // Or BASIC, HEADERS
            }
        )
        // Interceptor to add Pexels API Key header
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", BuildConfig.PEXELS_API_KEY) // Use the key from BuildConfig
                .build()
            chain.proceed(request)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    // Create an HTTP client with logging
    private fun createOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    // Retrofit instance for your existing API (if any)
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient) // Use the client with interceptors
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Retrofit instance specifically for Pexels API
    fun providePexelsRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(PEXELS_BASE_URL)
        .client(okHttpClient) // Use the client with interceptors (Authorization interceptor is there)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

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


    // Provide the Pexels API service
    fun providePexelsApiService(): PexelsApiService {
        return providePexelsRetrofit().create(PexelsApiService::class.java)
    }
} 