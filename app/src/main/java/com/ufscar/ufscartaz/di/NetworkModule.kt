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


object NetworkModule {
    
    private const val BASE_URL = "https://api.ufscartaz.com/"

    private const val PEXELS_BASE_URL = "https://api.pexels.com/v1/"

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // Or BASIC, HEADERS
            }
        )
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
    
    private fun createOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun providePexelsRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(PEXELS_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    fun provideApiService(): ApiService {
        return FakeApiService()
    }


    fun providePexelsApiService(): PexelsApiService {
        return providePexelsRetrofit().create(PexelsApiService::class.java)
    }
} 