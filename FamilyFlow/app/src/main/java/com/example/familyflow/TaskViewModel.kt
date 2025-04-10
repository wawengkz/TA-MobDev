package com.example.familyflow

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.familyflow.api.RetrofitClient
import com.example.familyflow.api.TaskRequest
import com.example.familyflow.api.TaskResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class TaskViewModel : ViewModel() {
    private val TAG = "TaskViewModel"

    // Using RetrofitClient directly instead of TaskRepository
    private val taskApiService = RetrofitClient.taskApiService

    // State for tasks
    private val _tasks = MutableStateFlow<List<TaskResponse>>(emptyList())
    val tasks: StateFlow<List<TaskResponse>> = _tasks.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun getTasksByRoomType(roomType: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = taskApiService.getTasksByRoomType(roomType)
                if (response.isSuccessful) {
                    _tasks.value = response.body() ?: emptyList()
                    Log.d(TAG, "Fetched ${_tasks.value.size} tasks for $roomType")
                } else {
                    _error.value = "Error: ${response.code()}"
                    Log.e(TAG, "Error fetching tasks: ${response.code()}")
                }
            } catch (e: HttpException) {
                _error.value = "Network error: ${e.message}"
                Log.e(TAG, "Network error", e)
            } catch (e: IOException) {
                _error.value = "Connection error: ${e.message}"
                Log.e(TAG, "Connection error", e)
            } catch (e: Exception) {
                _error.value = "Unexpected error: ${e.message}"
                Log.e(TAG, "Unexpected error", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createTask(task: TaskRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                Log.d(TAG, "Creating task with days: ${task.days}")

                val response = taskApiService.createTask(task)
                if (response.isSuccessful) {
                    // Refresh the task list for the room type
                    getTasksByRoomType(task.roomType)
                    Log.d(TAG, "Task created successfully")
                } else {
                    _error.value = "Failed to create task: ${response.code()}"
                    Log.e(TAG, "Error creating task: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.value = "Error creating task: ${e.message}"
                Log.e(TAG, "Exception creating task", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTask(taskId: Long, task: TaskRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = taskApiService.updateTask(taskId, task)
                if (response.isSuccessful) {
                    // Refresh the task list for the room type
                    getTasksByRoomType(task.roomType)
                    Log.d(TAG, "Task updated successfully")
                } else {
                    _error.value = "Failed to update task: ${response.code()}"
                    Log.e(TAG, "Error updating task: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.value = "Error updating task: ${e.message}"
                Log.e(TAG, "Exception updating task", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTask(taskId: Long, roomType: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = taskApiService.deleteTask(taskId)
                if (response.isSuccessful) {
                    // Refresh the task list for the room type
                    getTasksByRoomType(roomType)
                    Log.d(TAG, "Task deleted successfully")
                } else {
                    _error.value = "Failed to delete task: ${response.code()}"
                    Log.e(TAG, "Error deleting task: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.value = "Error deleting task: ${e.message}"
                Log.e(TAG, "Exception deleting task", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteAllTasksByRoomType(roomType: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = taskApiService.deleteAllTasksByRoomType(roomType)
                if (response.isSuccessful) {
                    // Refresh the task list (which should now be empty)
                    _tasks.value = emptyList()
                    Log.d(TAG, "All tasks deleted for $roomType")
                } else {
                    _error.value = "Failed to delete tasks: ${response.code()}"
                    Log.e(TAG, "Error deleting tasks by room type: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.value = "Error deleting tasks: ${e.message}"
                Log.e(TAG, "Exception deleting tasks by room type", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}