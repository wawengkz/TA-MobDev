package com.example.familyflow.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApiService {
    @POST("users/register")
    suspend fun registerUser(@Body user: UserRequest): Response<UserResponse>

    @POST("users/login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<UserResponse>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") userId: Long): Response<UserResponse>

    @GET("users/check-username/{username}")
    suspend fun checkUsernameExists(@Path("username") username: String): Response<ExistsResponse>

    @GET("users/check-email/{email}")
    suspend fun checkEmailExists(@Path("email") email: String): Response<ExistsResponse>
}

data class UserRequest(
    val username: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val usernameOrEmail: String,
    val password: String
)

data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val token: String? = null
)

data class ExistsResponse(
    val exists: Boolean
)