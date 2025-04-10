package com.example.familyflow

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.familyflow.api.RetrofitClient
import com.example.familyflow.api.TaskRequest
import com.example.familyflow.api.TaskResponse
import com.example.familyflow.ui.theme.FamilyFlowTheme
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class KitchenActivity : ComponentActivity() {

    // Add shared preferences
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize shared preferences
        prefs = getSharedPreferences("FamilyFlowTasks", Context.MODE_PRIVATE)

        setContent {
            FamilyFlowTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                val context = LocalContext.current

                // State for the API tasks
                var tasks by remember { mutableStateOf<List<TaskResponse>>(emptyList()) }
                var isLoading by remember { mutableStateOf(true) }

                // Function to fetch tasks from API
                suspend fun fetchTasks() {
                    try {
                        isLoading = true
                        val response = RetrofitClient.taskApiService.getTasksByRoomType("kitchen")
                        if (response.isSuccessful) {
                            tasks = response.body() ?: emptyList()

                            // If tasks are available, save the first non-completed task
                            if (tasks.isNotEmpty()) {
                                val firstActiveTask = tasks.firstOrNull { !it.isDone }
                                firstActiveTask?.let { task ->
                                    saveLatestTask(
                                        context,
                                        task.name,
                                        "Kitchen",
                                        task.assignedTo ?: "Unassigned"
                                    )
                                }
                            }

                            // Debug log to check fetched tasks
                            Log.d("KitchenActivity", "Fetched ${tasks.size} tasks")
                            tasks.forEach { task ->
                                Log.d("KitchenActivity", "Task: ${task.name}, Days: ${task.days}")
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Error: ${response.code()}")
                            }
                        }
                    } catch (e: HttpException) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Network error: ${e.message}")
                        }
                    } catch (e: IOException) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Connection error: ${e.message}")
                        }
                    } catch (e: Exception) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Unexpected error: ${e.message}")
                        }
                    } finally {
                        isLoading = false
                    }
                }

                // Function to update task on API
                suspend fun updateTask(task: TaskResponse) {
                    try {
                        isLoading = true
                        val taskRequest = TaskRequest(
                            name = task.name,
                            days = task.days,
                            assignedTo = task.assignedTo,
                            isDone = task.isDone,
                            roomType = "kitchen"
                        )

                        val response = RetrofitClient.taskApiService.updateTask(task.id, taskRequest)
                        if (response.isSuccessful) {
                            fetchTasks() // Refresh tasks after update
                            Toast.makeText(context, "Task updated successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Failed to update task: ${response.code()}")
                            }
                        }
                    } catch (e: Exception) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Error updating task: ${e.message}")
                        }
                    } finally {
                        isLoading = false
                    }
                }

                // Function to delete task from API
                suspend fun deleteTask(taskId: Long) {
                    try {
                        isLoading = true
                        val response = RetrofitClient.taskApiService.deleteTask(taskId)
                        if (response.isSuccessful) {
                            fetchTasks() // Refresh tasks after delete
                            Toast.makeText(context, "Task deleted successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Failed to delete task: ${response.code()}")
                            }
                        }
                    } catch (e: Exception) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Error deleting task: ${e.message}")
                        }
                    } finally {
                        isLoading = false
                    }
                }

                // Function to create task on API
                suspend fun createTask(task: Task) {
                    try {
                        isLoading = true

                        // Debug: Print days before creating request
                        Log.d("KitchenActivity", "Creating task with days: ${task.days}")

                        // Make sure we're creating a non-empty set
                        if (task.days.isEmpty()) {
                            Log.e("KitchenActivity", "Attempted to create task with empty days")
                            scope.launch {
                                snackbarHostState.showSnackbar("Cannot create task with no days selected")
                            }
                            isLoading = false
                            return
                        }

                        val taskRequest = TaskRequest(
                            name = task.name,
                            days = task.days.toSet(), // Ensure it's a non-empty Set<String>
                            assignedTo = task.assignedTo,
                            isDone = task.isDone,
                            roomType = "kitchen"
                        )

                        // More debugging
                        Log.d("KitchenActivity", "TaskRequest days: ${taskRequest.days}")

                        val response = RetrofitClient.taskApiService.createTask(taskRequest)
                        if (response.isSuccessful) {
                            // Save the newly created task as the latest task
                            saveLatestTask(
                                context,
                                task.name,
                                "Kitchen",
                                task.assignedTo ?: "Unassigned"
                            )

                            fetchTasks() // Refresh tasks after create
                            Toast.makeText(context, "Task created successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Failed to create task: ${response.code()}")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("KitchenActivity", "Error creating task", e)
                        scope.launch {
                            snackbarHostState.showSnackbar("Error creating task: ${e.message}")
                        }
                    } finally {
                        isLoading = false
                    }
                }

                // Fetch tasks when the screen is first created
                LaunchedEffect(Unit) {
                    fetchTasks()
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { paddingValues ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        NavHost(
                            navController = navController,
                            startDestination = "kitchen",
                            modifier = Modifier.padding(paddingValues)
                        ) {
                            composable("kitchen") {
                                KitchenScreen(
                                    navController = navController,
                                    // Convert API TaskResponse to your UI Task model
                                    tasks = tasks.map { response ->
                                        Task(
                                            id = response.id,
                                            name = response.name,
                                            days = response.days,
                                            assignedTo = response.assignedTo,
                                            isDone = response.isDone
                                        )
                                    },
                                    onTaskUpdated = { updatedTask ->
                                        // Find the API task by ID and update it
                                        val apiTask = tasks.find { it.id == updatedTask.id }
                                        apiTask?.let { task ->
                                            scope.launch {
                                                updateTask(task.copy(isDone = updatedTask.isDone))
                                            }
                                        }
                                    },
                                    onTaskDeleted = { taskToDelete ->
                                        scope.launch {
                                            deleteTask(taskToDelete.id)
                                        }
                                    }
                                )
                            }
                            composable("assignTask") {
                                AssignTaskScreen(
                                    navController = navController,
                                    onTaskCreated = { newTask ->
                                        scope.launch {
                                            createTask(newTask)
                                            navController.navigateUp()
                                        }
                                    }
                                )
                            }
                        }

                        // Show loading indicator if needed
                        if (isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }

    // Helper method to save the latest task
    private fun saveLatestTask(context: Context, name: String, room: String, assignee: String) {
        val prefs = context.getSharedPreferences("FamilyFlowTasks", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("latest_task_name", name)
            .putString("latest_task_room", room)
            .putString("latest_task_assignee", assignee)
            .apply()
    }

    // Updated Task data class to include ID from API
    data class Task(
        val id: Long = 0, // Add ID field for API interaction
        val name: String,
        val days: Set<String>,
        val assignedTo: String?,
        var isDone: Boolean = false
    )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun KitchenScreen(
        navController: NavHostController,
        tasks: List<Task>,
        onTaskUpdated: (Task) -> Unit,
        onTaskDeleted: (Task) -> Unit
    ) {
        // Calculate completed tasks and total tasks
        var completedTasks by remember { mutableStateOf(0) }
        var totalTasks by remember { mutableStateOf(0) }

        // Update counters when tasks change
        LaunchedEffect(tasks) {
            completedTasks = tasks.count { it.isDone }
            totalTasks = tasks.size
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Kitchen", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { /* Handle navigation back if needed */ }) {
                            // Updated to use AutoMirrored version
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF81C784))
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF81C784))
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (tasks.isEmpty()) {
                        Text("No tasks available", fontSize = 20.sp, color = Color.White)
                    } else {
                        tasks.forEach { task ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(Color(0xFF66BB6A), RoundedCornerShape(8.dp))
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        task.name,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                    Text(
                                        "Assigned to: ${task.assignedTo ?: "Unassigned"}",
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )

                                    // Check if days is empty and handle it gracefully
                                    val daysText = if (task.days.isNotEmpty()) {
                                        "Days: ${task.days.joinToString(", ")}"
                                    } else {
                                        "Days: None"
                                    }

                                    Text(
                                        daysText,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                }
                                Checkbox(
                                    checked = task.isDone,
                                    onCheckedChange = {
                                        // Create a new task with the updated isDone value
                                        val updatedTask = task.copy(isDone = it)
                                        onTaskUpdated(updatedTask)
                                    }
                                )
                                IconButton(
                                    onClick = {
                                        onTaskDeleted(task)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Task",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { navController.navigate("assignTask") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text("ADD TASK", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                // Task completion counters and image at the bottom
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "$completedTasks ✔",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "${totalTasks - completedTasks} ❌",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Image(
                        painter = painterResource(id = R.drawable.kitchen),
                        contentDescription = "Kitchen Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AssignTaskScreen(navController: NavHostController, onTaskCreated: (Task) -> Unit) {
        var taskName by remember { mutableStateOf("") }
        var selectedDays by remember { mutableStateOf(mutableSetOf<String>()) }
        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val familyMembers = listOf("Father", "Mother", "Child 1", "Child 2")
        var selectedMember by remember { mutableStateOf<String?>(null) }

        // Add state for validation errors
        var taskNameError by remember { mutableStateOf<String?>(null) }
        var daysError by remember { mutableStateOf<String?>(null) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Task Name", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            // Updated to use AutoMirrored version
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF81C784))
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = taskName,
                    onValueChange = {
                        taskName = it
                        // Clear error when user types
                        taskNameError = null
                    },
                    label = { Text("Enter Task Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = taskNameError != null,
                    supportingText = {
                        taskNameError?.let {
                            Text(
                                text = it,
                                color = Color.Red
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text("Do Every", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                if (daysError != null) {
                    Text(
                        text = daysError!!,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    daysOfWeek.chunked(4).forEach { rowDays ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            rowDays.forEach { day ->
                                Button(
                                    onClick = {
                                        if (selectedDays.contains(day)) {
                                            selectedDays.remove(day)
                                        } else {
                                            selectedDays.add(day)
                                        }
                                        // Clear error when user selects days
                                        daysError = null
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedDays.contains(day)) Color(
                                            0xFF4CAF50
                                        ) else Color.Gray
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(day, color = Color.White)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Assign To", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    familyMembers.forEach { member ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMember = member }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = selectedMember == member,
                                onCheckedChange = { isChecked ->
                                    selectedMember = if (isChecked) member else null
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(member, fontSize = 18.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        // Validate inputs
                        var isValid = true

                        if (taskName.isBlank()) {
                            taskNameError = "Please enter a task name"
                            isValid = false
                        }

                        if (selectedDays.isEmpty()) {
                            daysError = "Please select at least one day"
                            isValid = false
                        }

                        if (isValid) {
                            // Debug: Print selected days
                            Log.d("AssignTaskScreen", "Selected days: $selectedDays")

                            val newTask = Task(
                                name = taskName,
                                days = selectedDays.toSet(),  // Explicitly convert to Set
                                assignedTo = selectedMember,
                                isDone = false
                            )
                            onTaskCreated(newTask)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("CREATE", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun KitchenScreenPreview() {
        FamilyFlowTheme {
            KitchenScreen(
                navController = rememberNavController(),
                tasks = mutableListOf(Task(1, "Example Task", setOf("Mon", "Wed"), "Father")),
                onTaskUpdated = {},
                onTaskDeleted = {}
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun AssignTaskScreenPreview() {
        FamilyFlowTheme {
            AssignTaskScreen(navController = rememberNavController()) { }
        }
    }
}