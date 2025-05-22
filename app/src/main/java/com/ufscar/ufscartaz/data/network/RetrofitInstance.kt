package com.ufscar.ufscartaz.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.Interceptor

object RetrofitInstance {
    private val client = OkHttpClient.Builder().addInterceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3MmFiNjE3ZWM4NzQyNjFjNmUwZDVmYzgwZmJiYTk3MyIsIm5iZiI6MTcxMDM3MjU0My4wNjIsInN1YiI6IjY1ZjIzNmJmZmJlMzZmMDE4NWVmZTFhOSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.DehvOWdRyfvipAZBqK1z43QtwpcM89bpMUXOG9O6nQQ")
            .build()
        chain.proceed(request)
    }.build()

    val api: MovieApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieApiService::class.java)
    }
}
