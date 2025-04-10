package com.example.familyflow.api

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Replace 127.0.0.1 with your computer's actual IP address on your network
    // For example: "http://192.168.1.100:8000/"
    // You can find your IP by running 'ipconfig' on Windows or 'ifconfig' on Mac/Linux
    private const val BASE_URL = "http://192.168.1.21:8000/"  // Special IP for Android Emulator to reach host machine

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val token = SessionManager.getAuthToken()

        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        chain.proceed(requestBuilder.build())
    }

    // 🧩 Enable lenient Gson
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson)) // Use lenient gson
        .build()

    val userApiService: UserApiService = retrofit.create(UserApiService::class.java)
    val taskApiService: TaskApiService = retrofit.create(TaskApiService::class.java)
    val householdApiService: HouseholdApiService = retrofit.create(HouseholdApiService::class.java)
}