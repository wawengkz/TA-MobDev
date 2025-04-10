package com.example.familyflow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.familyflow.api.HouseholdRequest
import com.example.familyflow.api.RetrofitClient
import com.example.familyflow.ui.theme.FamilyFlowTheme
import kotlinx.coroutines.launch
import kotlin.random.Random

class CHouseholdActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FamilyFlowTheme {
                CreateHouseholdScreen(
                    onHouseholdCreated = { householdName, householdCode ->
                        // Save household info to shared preferences
                        val sharedPrefs = getSharedPreferences("FamilyFlowPrefs", MODE_PRIVATE)
                        sharedPrefs.edit()
                            .putString("household_name", householdName)
                            .putString("household_code", householdCode)
                            .apply()

                        // Navigate to ManageHousehold screen
                        val intent = Intent(this, ManageHousehold::class.java)
                        startActivity(intent)
                        finish() // Close this activity so user can't go back
                    }
                )
            }
        }
    }
}

@Composable
fun CreateHouseholdScreen(onHouseholdCreated: (String, String) -> Unit) {
    val householdName = remember { mutableStateOf("") }
    val adminRole = remember { mutableStateOf("") }
    val members = remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.f),
            contentDescription = "Logo",
            modifier = Modifier
                .size(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Create Household",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1976D2),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // OutlinedTextField for Household Name
        OutlinedTextField(
            value = householdName.value,
            onValueChange = { householdName.value = it },
            label = { Text("Household Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            isError = errorMessage != null && householdName.value.isEmpty()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // OutlinedTextField for Admin Role
        OutlinedTextField(
            value = adminRole.value,
            onValueChange = { adminRole.value = it },
            label = { Text("Admin Role") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            isError = errorMessage != null && adminRole.value.isEmpty()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // OutlinedTextField for Adding Members
        OutlinedTextField(
            value = members.value,
            onValueChange = { members.value = it },
            label = { Text("Add Members (comma separated)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Display the error message if any
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Create Household Button
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                color = Color(0xFF1976D2)
            )
        } else {
            CHouseholdGradientButton(
                text = "Create Household",
                onClick = {
                    // Validate inputs
                    if (householdName.value.isBlank() || adminRole.value.isBlank()) {
                        errorMessage = "Household name and admin role are required"
                        return@CHouseholdGradientButton
                    }

                    isLoading = true
                    errorMessage = null

                    // Generate a random 4-digit code
                    val householdCode = Random.nextInt(1000, 10000).toString()

                    // Parse members list
                    val membersList = if (members.value.isBlank()) {
                        emptyList()
                    } else {
                        members.value.split(",").map { it.trim() }
                    }

                    coroutineScope.launch {
                        try {
                            // Create the household request object
                            val householdRequest = HouseholdRequest(
                                name = householdName.value,
                                adminRole = adminRole.value,
                                members = membersList,
                                code = householdCode
                            )

                            // Make API call to create household
                            try {
                                val response = RetrofitClient.householdApiService.createHousehold(householdRequest)

                                if (response.isSuccessful) {
                                    // Success! Notify the parent activity and navigate
                                    Toast.makeText(context, "Household created successfully! Your code is: $householdCode", Toast.LENGTH_LONG).show()
                                    onHouseholdCreated(householdName.value, householdCode)
                                } else {
                                    // API error - But we'll proceed anyway with the fallback solution
                                    Log.e("CreateHousehold", "API error: ${response.code()}, using fallback solution")
                                    handleFallbackSolution(householdName.value, householdCode, context, onHouseholdCreated)
                                }
                            } catch (e: Exception) {
                                // Network/API error - Use fallback solution
                                Log.e("CreateHousehold", "API error, using fallback solution", e)
                                handleFallbackSolution(householdName.value, householdCode, context, onHouseholdCreated)
                            }
                        } catch (e: Exception) {
                            Log.e("CreateHousehold", "Unexpected error", e)
                            errorMessage = "An unexpected error occurred: ${e.message}"
                            isLoading = false
                        }
                    }
                }
            )
        }

        // Add a note about the household code
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "A 4-digit code will be generated for your household. You'll need this code to enter the household later.",
            color = Color.Gray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

// Fallback solution when API fails
private suspend fun handleFallbackSolution(
    householdName: String,
    householdCode: String,
    context: android.content.Context,
    onHouseholdCreated: (String, String) -> Unit
) {
    // Simply proceed with the local storage of the household info
    // In a real app, you might want to queue this for sync later
    Toast.makeText(
        context,
        "Couldn't connect to server. Your household has been created locally.\nYour code is: $householdCode",
        Toast.LENGTH_LONG
    ).show()

    // Still call the callback to continue the flow
    onHouseholdCreated(householdName, householdCode)
}

@Composable
fun CHouseholdGradientButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF4FC3F7), // Light blue
                            Color(0xFF039BE5)  // Dark blue
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text, color = Color.White, fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateHouseholdScreenPreview() {
    FamilyFlowTheme {
        CreateHouseholdScreen(onHouseholdCreated = { _, _ -> })
    }
}