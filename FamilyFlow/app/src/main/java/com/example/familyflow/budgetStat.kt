package com.example.familyflow

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun BillsStatScreen(navController: NavController) { // Accept NavController as a parameter
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4FC3F7), // Light blue start
                        Color(0xFF039BE5)  // Dark blue end
                    )
                )
            )
            .padding(16.dp)
    ) {
        BillsStatTopAppBar(navController) // Pass navController to TopAppBar

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BillsStatTitle()
            BillStatisticsSection()
            BillsStatListSection()
        }
    }
}

@Composable
fun BillsStatTopAppBar(navController: NavController) { // Accept NavController as a parameter
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* Handle home button click */ }) {
            Image(
                painter = painterResource(id = R.drawable.home),
                contentDescription = "Home",
                modifier = Modifier.size(36.dp)
            )
        }

        Image(
            painter = painterResource(id = R.drawable.f),
            contentDescription = "F Logo",
            modifier = Modifier.size(36.dp)
        )

        IconButton(onClick = { navController.navigateUp() }) { // Back button functionality
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back",
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
fun BillsStatTitle() {
    Text(
        text = "Budget Port",
        style = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun BillStatisticsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Paid", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50)))
        Text("Electricity Bill:", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2)))
        Text("15,000 PHP", style = TextStyle(fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1)))

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.stats),
            contentDescription = "Statistics Graph",
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("2024", fontSize = 14.sp, color = Color.Gray)
            Text("May", fontSize = 14.sp, color = Color.Gray)
            Text("June", fontSize = 14.sp, color = Color.Gray)
            Text("July", fontSize = 14.sp, color = Color.Gray)
            Text("Aug", fontSize = 14.sp, color = Color.Gray)
            Text("Sept", fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun BillsStatListSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(24.dp)
    ) {
        Text("Bills List:", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold))

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StatBillItem("Electricity:", "15,000 PHP", Modifier.weight(1f))
            StatBillItem("Water Bill:", "2,000 PHP", Modifier.weight(1f))
        }
    }
}

@Composable
fun StatBillItem(name: String, amount: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFFE3F2FD), shape = RoundedCornerShape(8.dp))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(name, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(amount, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, fontSize = 16.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBillsStatScreen() {
    val navController = rememberNavController() // Use a dummy NavController for preview
    BillsStatScreen(navController)
}
