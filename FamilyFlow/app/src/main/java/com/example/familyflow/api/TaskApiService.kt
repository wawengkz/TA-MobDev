package com.example.familyflow.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TaskApiService {
    @GET("tasks/")
    suspend fun getAllTasks(): Response<List<TaskResponse>>

    @GET("tasks/room/{roomType}/")
    suspend fun getTasksByRoomType(@Path("roomType") roomType: String): Response<List<TaskResponse>>

    @POST("tasks/")
    suspend fun createTask(@Body taskRequest: TaskRequest): Response<TaskResponse>

    @PUT("tasks/{id}/")
    suspend fun updateTask(@Path("id") id: Long, @Body taskRequest: TaskRequest): Response<TaskResponse>

    @DELETE("tasks/{id}/")
    suspend fun deleteTask(@Path("id") id: Long): Response<Void>

    @DELETE("tasks/room/{roomType}/delete/")
    suspend fun deleteAllTasksByRoomType(@Path("roomType") roomType: String): Response<Void>
}

data class TaskRequest(
    val name: String,
    val days: Set<String>,
    val assignedTo: String?,
    val isDone: Boolean,
    val roomType: String
)

data class TaskResponse(
    val id: Long,
    val name: String,
    val days: Set<String>,
    val assignedTo: String?,
    val isDone: Boolean,
    val roomType: String
)