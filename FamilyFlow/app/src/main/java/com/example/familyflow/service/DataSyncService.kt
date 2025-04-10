package com.example.familyflow.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.familyflow.api.RetrofitClient
import com.example.familyflow.data.database.AppDatabase
import com.example.familyflow.data.entity.TaskEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

class DataSyncService : Service() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("DataSyncService started")

        // Start the sync operation
        syncPendingData()

        return START_NOT_STICKY
    }

    private fun syncPendingData() {
        scope.launch {
            try {
                // Sync tasks for each room type
                syncTasksByRoomType("kitchen")
                syncTasksByRoomType("bathroom")
                syncTasksByRoomType("livingroom")

                // Add other sync operations as needed

                Timber.d("Data sync completed successfully")
            } catch (e: Exception) {
                Timber.e(e, "Error during data sync")
            } finally {
                // Stop the service when done
                stopSelf()
            }
        }
    }

    private suspend fun syncTasksByRoomType(roomType: String) {
        try {
            val database = AppDatabase.getDatabase(applicationContext)
            val taskDao = database.taskDao()

            // Get all tasks for this room type
            val tasks = taskDao.getTasksByRoomTypeNonFlow(roomType)

            for (task in tasks) {
                // Attempt to sync each task
                syncTask(task)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error syncing tasks for room type: $roomType")
        }
    }

    private suspend fun syncTask(task: TaskEntity) {
        try {
            // Map to API request
            val taskRequest = com.example.familyflow.api.TaskRequest(
                name = task.name,
                days = task.days,
                assignedTo = task.assignedTo,
                isDone = task.isDone,
                roomType = task.roomType
            )

            // Try to update or create task
            val updateResponse =
                if (task.id > 0) {
                    // Task has ID, try to update
                    RetrofitClient.taskApiService.updateTask(task.id, taskRequest)
                } else {
                    // Task has no ID, create new one
                    RetrofitClient.taskApiService.createTask(taskRequest)
                }

            // Handle response
            if (updateResponse.isSuccessful) {
                val apiTask = updateResponse.body()
                if (apiTask != null) {
                    // Update local task with server ID if it was newly created
                    if (task.id == 0L) {
                        val database = AppDatabase.getDatabase(applicationContext)
                        val taskDao = database.taskDao()

                        val updatedTask = task.copy(id = apiTask.id)
                        taskDao.insertTask(updatedTask)
                    }

                    Timber.d("Task synced successfully: ${task.name}")
                }
            } else {
                Timber.w("Failed to sync task: ${task.name}, status: ${updateResponse.code()}")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error syncing task: ${task.name}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}