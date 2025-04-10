package com.example.familyflow

// Ensure all necessary imports
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.familyflow.api.RetrofitClient
import com.example.familyflow.ui.ChatsActivity
import com.example.familyflow.ui.theme.FamilyFlowTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class ManageHousehold : ComponentActivity() {

    // Data class to represent chores for display
    data class ChoreInfo(
        val name: String,
        val room: String,
        val assignee: String
    )

    // Shared preferences key
    private val PREFS_NAME = "FamilyFlowTasks"
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize shared preferences
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        setContent {
            FamilyFlowTheme {
                val chores = remember { mutableStateOf<List<ChoreInfo>>(emptyList()) }
                var isLoading by remember { mutableStateOf(true) }

                // Function to fetch all chores from all rooms
                LaunchedEffect(Unit) {
                    isLoading = true
                    try {
                        // Fetch tasks from each room type
                        val kitchenTasks = withContext(Dispatchers.IO) {
                            try {
                                val response = RetrofitClient.taskApiService.getTasksByRoomType("kitchen")
                                if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
                            } catch (e: Exception) {
                                Log.e("ManageHousehold", "Error fetching kitchen tasks", e)
                                emptyList()
                            }
                        }

                        val bathroomTasks = withContext(Dispatchers.IO) {
                            try {
                                val response = RetrofitClient.taskApiService.getTasksByRoomType("bathroom")
                                if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
                            } catch (e: Exception) {
                                Log.e("ManageHousehold", "Error fetching bathroom tasks", e)
                                emptyList()
                            }
                        }

                        val livingroomTasks = withContext(Dispatchers.IO) {
                            try {
                                val response = RetrofitClient.taskApiService.getTasksByRoomType("livingroom")
                                if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
                            } catch (e: Exception) {
                                Log.e("ManageHousehold", "Error fetching livingroom tasks", e)
                                emptyList()
                            }
                        }

                        // Convert all tasks to ChoreInfo objects
                        val allChores = mutableListOf<ChoreInfo>()

                        kitchenTasks.forEach { task ->
                            if (!task.isDone) {
                                allChores.add(ChoreInfo(
                                    name = task.name,
                                    room = "Kitchen",
                                    assignee = task.assignedTo ?: "Unassigned"
                                ))
                            }
                        }

                        bathroomTasks.forEach { task ->
                            if (!task.isDone) {
                                allChores.add(ChoreInfo(
                                    name = task.name,
                                    room = "Bathroom",
                                    assignee = task.assignedTo ?: "Unassigned"
                                ))
                            }
                        }

                        livingroomTasks.forEach { task ->
                            if (!task.isDone) {
                                allChores.add(ChoreInfo(
                                    name = task.name,
                                    room = "Living Room",
                                    assignee = task.assignedTo ?: "Unassigned"
                                ))
                            }
                        }

                        chores.value = allChores

                    } catch (e: Exception) {
                        Log.e("ManageHousehold", "Error fetching chores", e)
                        chores.value = emptyList()
                    } finally {
                        isLoading = false
                    }
                }

                ManageHouseholdScreen(
                    chores = chores.value,
                    isLoading = isLoading,
                    onKitchenClick = {
                        val intent = Intent(this, KitchenActivity::class.java)
                        startActivity(intent)
                    },
                    onBathroomClick = {
                        val intent = Intent(this, BathroomActivity::class.java)
                        startActivity(intent)
                    },
                    onLivingRoomClick = {
                        val intent = Intent(this, LivingRoomActivity::class.java)
                        startActivity(intent)
                    },
                    onChatsClick = {
                        val intent = Intent(this, ChatsActivity::class.java)
                        startActivity(intent)
                    },
                    onEventCalendarClick = {
                        val intent = Intent(this, CalendarEvent::class.java)
                        startActivity(intent)
                    },
                    onBudgetClick = {
                        val intent = Intent(this, BudgetActivity::class.java)
                        startActivity(intent)
                    },
                    onManageHouseholdClick = {
                        val intent = Intent(this, mHousehold::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }

    companion object {
        // Helper method to save a task - keeping this for compatibility but not actually needed now
        fun saveLatestTask(context: Context, name: String, room: String, assignee: String) {
            val prefs = context.getSharedPreferences("FamilyFlowTasks", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("latest_task_name", name)
                .putString("latest_task_room", room)
                .putString("latest_task_assignee", assignee)
                .apply()
        }
    }
}

@Composable
fun ManageHouseholdScreen(
    chores: List<ManageHousehold.ChoreInfo>,
    isLoading: Boolean,
    onKitchenClick: () -> Unit,
    onBathroomClick: () -> Unit,
    onLivingRoomClick: () -> Unit,
    onChatsClick: () -> Unit,
    onEventCalendarClick: () -> Unit,
    onBudgetClick: () -> Unit,
    onManageHouseholdClick: () -> Unit
) {
    var touchMessage by remember { mutableStateOf("") }
    var isNavigationVisible by remember { mutableStateOf(false) }
    var isNotificationVisible by remember { mutableStateOf(false) }

    // State for premium feature dialog
    var showPremiumDialog by remember { mutableStateOf(false) }
    var currentPremiumFeature by remember { mutableStateOf("") }

    val navigationHeight by animateDpAsState(
        targetValue = if (isNavigationVisible) 300.dp else 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = "Navigation Height"
    )

    val notificationHeight by animateDpAsState(
        targetValue = if (isNotificationVisible) 300.dp else 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = "Notification Height"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ManageHouseholdTopAppBar(onNotificationClick = { isNotificationVisible = true })

            Spacer(modifier = Modifier.height(16.dp))

            // Family Name
            Text(
                text = "GOMEZ FAMILY",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2),
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Schedule Chore Box with multiple chores display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp) // Increased height to accommodate multiple chores
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF4FC3F7), Color(0xFF039BE5))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
                    .clickable { onManageHouseholdClick() }
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxSize()
                ) {

                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "Schedule Chore",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Left
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isLoading) {
                            Text(
                                text = "Loading chores...",
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        } else if (chores.isNotEmpty()) {
                            // Show all chores
                            chores.forEach { chore ->
                                Text(
                                    text = "• ${chore.name}",
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "  ${chore.room} - ${chore.assignee}",
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        } else {
                            // Show default message if no chores are available
                            Text(
                                text = "• No active tasks",
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Room Boxes Row 1
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                DraggableBoxItem(
                    text = "Kitchen",
                    backgroundColorStart = Color(0xFF8BC34A),
                    backgroundColorEnd = Color(0xFF689F38),
                    imageResource = R.drawable.kitchen,
                    iconSize = 60.dp,
                    padding = 12.dp,
                    modifier = Modifier.weight(1f),
                    onClick = onKitchenClick
                )
                DraggableBoxItem(
                    text = "Bathroom",
                    backgroundColorStart = Color(0xFFF48FB1),
                    backgroundColorEnd = Color(0xFFC2185B),
                    imageResource = R.drawable.bathroom,
                    iconSize = 60.dp,
                    padding = 12.dp,
                    modifier = Modifier.weight(1f),
                    onClick = onBathroomClick
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Room Boxes Row 2
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                DraggableBoxItem(
                    text = "Living Room",
                    backgroundColorStart = Color(0xFFB0BEC5),
                    backgroundColorEnd = Color(0xFF455A64),
                    imageResource = R.drawable.livingroom,
                    iconSize = 60.dp,
                    padding = 12.dp,
                    modifier = Modifier.weight(1f),
                    onClick = onLivingRoomClick
                )
                DraggableBoxItem(
                    text = "Office",
                    backgroundColorStart = Color(0xFF9575CD),
                    backgroundColorEnd = Color(0xFF512DA8),
                    imageResource = R.drawable.office,
                    iconSize = 60.dp,
                    padding = 12.dp,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        currentPremiumFeature = "Office"
                        showPremiumDialog = true
                    },
                    isPremiumFeature = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Room Boxes Row 3
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                DraggableBoxItem(
                    text = "Garden",
                    backgroundColorStart = Color(0xFFD1C4E9),
                    backgroundColorEnd = Color(0xFF7E57C2),
                    imageResource = R.drawable.garden,
                    iconSize = 60.dp,
                    padding = 12.dp,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        currentPremiumFeature = "Garden"
                        showPremiumDialog = true
                    },
                    isPremiumFeature = true
                )
                DraggableBoxItem(
                    text = "",
                    backgroundColorStart = Color(0xFF90CAF9),
                    backgroundColorEnd = Color(0xFF42A5F5),
                    imageResource = R.drawable.add,
                    iconSize = 80.dp,
                    padding = 8.dp,
                    modifier = Modifier.weight(1f),
                    showText = false,
                    onClick = {
                        currentPremiumFeature = "Add Custom Room"
                        showPremiumDialog = true
                    },
                    isPremiumFeature = true
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Navigation Button at the Bottom
            if (!isNavigationVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF42A5F5), Color(0xFF1976D2))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { isNavigationVisible = !isNavigationVisible },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.navigation),
                        contentDescription = "Navigation Icon",
                        modifier = Modifier.size(100.dp)
                    )
                }
            }

            // Navigation Drawer - Appears when isNavigationVisible is true
            if (isNavigationVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(navigationHeight)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF42A5F5), Color(0xFF90CAF9))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                        .clickable { isNavigationVisible = false }
                ) {
                    Column {
                        Text(
                            text = "Manage Household",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .clickable { onManageHouseholdClick() }
                        )
                        Text(
                            text = "Chats",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .clickable { onChatsClick() }
                        )
                        Text(
                            text = "Event Calendar",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .clickable { onEventCalendarClick() }
                        )
                        Text(
                            text = "Budget Port",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .clickable { onBudgetClick() }
                        )
                    }
                }
            }
        }

        // Premium Feature Dialog
        if (showPremiumDialog) {
            PremiumFeatureDialog(
                featureName = currentPremiumFeature,
                onDismiss = { showPremiumDialog = false }
            )
        }
    }
}

@Composable
fun PremiumFeatureDialog(
    featureName: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Title
                Text(
                    text = "Premium Feature",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                Text(
                    text = "The $featureName feature is available exclusively to Premium subscribers. Upgrade now to unlock all features!",
                    fontSize = 16.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Upgrade Button
                Button(
                    onClick = { onDismiss() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "UPGRADE TO PREMIUM",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Cancel Button
                Text(
                    text = "Maybe Later",
                    fontSize = 16.sp,
                    color = Color(0xFF1976D2),
                    modifier = Modifier
                        .clickable { onDismiss() }
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ManageHouseholdTopAppBar(onNotificationClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* Handle user icon click */ }) {
            Image(
                painter = painterResource(id = R.drawable.home),
                contentDescription = "home",
                modifier = Modifier.size(36.dp)
            )
        }

        Image(
            painter = painterResource(id = R.drawable.f),
            contentDescription = "F Logo",
            modifier = Modifier.size(36.dp)
        )

        IconButton(onClick = { onNotificationClick() }) {
            Image(
                painter = painterResource(id = R.drawable.notification),
                contentDescription = "Notification",
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
fun DraggableBoxItem(
    text: String,
    backgroundColorStart: Color,
    backgroundColorEnd: Color,
    imageResource: Int,
    iconSize: Dp,
    padding: Dp,
    modifier: Modifier = Modifier,
    showText: Boolean = true,
    isPremiumFeature: Boolean = false,
    onClick: () -> Unit = {}
) {
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }

    Box(
        modifier = modifier
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(backgroundColorStart, backgroundColorEnd)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(padding)
            .pointerInput(Unit) {
                // Only allow dragging for non-premium features
                if (!isPremiumFeature) {
                    detectDragGestures { _, dragAmount ->
                        offset = Offset(offset.x + dragAmount.x, offset.y + dragAmount.y)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Add a lock icon for premium features
            if (isPremiumFeature) {
                Box(
                    modifier = Modifier.size(iconSize)
                ) {
                    Image(
                        painter = painterResource(id = imageResource),
                        contentDescription = text,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                Image(
                    painter = painterResource(id = imageResource),
                    contentDescription = text,
                    modifier = Modifier.size(iconSize)
                )
            }

            if (showText && text.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = text,
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewManageHouseholdScreen() {
    FamilyFlowTheme {
        val previewChores = listOf(
            ManageHousehold.ChoreInfo("Clean Kitchen", "Kitchen", "Father"),
            ManageHousehold.ChoreInfo("Wash Dishes", "Kitchen", "Child 1"),
            ManageHousehold.ChoreInfo("Clean Bathroom", "Bathroom", "Mother")
        )

        ManageHouseholdScreen(
            chores = previewChores,
            isLoading = false,
            onKitchenClick = {},
            onBathroomClick = {},
            onLivingRoomClick = {},
            onChatsClick = {},
            onEventCalendarClick = {},
            onBudgetClick = {},
            onManageHouseholdClick = {}
        )
    }
}