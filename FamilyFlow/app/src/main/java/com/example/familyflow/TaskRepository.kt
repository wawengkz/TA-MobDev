package com.example.familyflow.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRepository {
    private val taskApiService = RetrofitClient.taskApiService
    private val TAG = "TaskRepository"

    suspend fun getTasksByRoomType(roomType: String): Result<List<TaskResponse>> = withContext(Dispatchers.IO) {
        try {
            val response = taskApiService.getTasksByRoomType(roomType)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Log.e(TAG, "Error getting tasks: ${response.code()} ${response.message()}")
                Result.failure(Exception("Failed to get tasks: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception getting tasks", e)
            Result.failure(e)
        }
    }

    suspend fun createTask(task: TaskRequest): Result<TaskResponse> = withContext(Dispatchers.IO) {
        try {
            // Log the task data to check what's being sent
            Log.d(TAG, "Creating task with days: ${task.days}")

            val response = taskApiService.createTask(task)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Log.e(TAG, "Error creating task: ${response.code()} ${response.message()}")
                Result.failure(Exception("Failed to create task: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception creating task", e)
            Result.failure(e)
        }
    }

    suspend fun updateTask(taskId: Long, task: TaskRequest): Result<TaskResponse> = withContext(Dispatchers.IO) {
        try {
            val response = taskApiService.updateTask(taskId, task)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Log.e(TAG, "Error updating task: ${response.code()} ${response.message()}")
                Result.failure(Exception("Failed to update task: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception updating task", e)
            Result.failure(e)
        }
    }

    suspend fun deleteTask(taskId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = taskApiService.deleteTask(taskId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Log.e(TAG, "Error deleting task: ${response.code()} ${response.message()}")
                Result.failure(Exception("Failed to delete task: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception deleting task", e)
            Result.failure(e)
        }
    }

    suspend fun deleteAllTasksByRoomType(roomType: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = taskApiService.deleteAllTasksByRoomType(roomType)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Log.e(TAG, "Error deleting tasks by room type: ${response.code()} ${response.message()}")
                Result.failure(Exception("Failed to delete tasks: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception deleting tasks by room type", e)
            Result.failure(e)
        }
    }
}