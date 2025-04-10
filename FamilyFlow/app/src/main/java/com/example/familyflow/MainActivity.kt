package com.example.familyflow

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.familyflow.ui.theme.FamilyFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FamilyFlowTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WelcomeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }

        // Move the navigation code here
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Optional: close MainActivity so it won't be accessible via back button
        }, 3000) // 3000 milliseconds = 3 seconds
    }
}

@Composable
fun WelcomeScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center // Center everything inside the Box
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_dump),
            contentDescription = "Family Flow Logo",
            modifier = Modifier
                .fillMaxWidth(1f) // Adjust width as needed for scaling
                .aspectRatio(1f), // Keeps the image aspect ratio
            contentScale = ContentScale.Fit // Fit the image within the specified dimensions
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    FamilyFlowTheme {
        WelcomeScreen()
    }
}
