package com.example.bustraveller.data.remote

import com.example.bustraveller.data.local.AuthManager
import com.example.bustraveller.data.remote.api.TrackingApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Change this to your backend server URL
    // For Android emulator, use http://10.0.2.2:3000
    // For physical device, use your computer's IP address
    private const val BASE_URL = "http://10.0.2.2:3000/api/"
    
    private var authManager: AuthManager? = null
    
    fun initialize(authManager: AuthManager) {
        RetrofitClient.authManager = authManager
    }
    
    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val token = authManager?.getAuthHeader()
        
        val requestBuilder = original.newBuilder()
        if (token != null) {
            requestBuilder.addHeader("Authorization", token)
        }
        
        chain.proceed(requestBuilder.build())
    }
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: TrackingApiService = retrofit.create(TrackingApiService::class.java)
}

