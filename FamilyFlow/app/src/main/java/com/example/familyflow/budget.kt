package com.example.familyflow

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // NavHost for navigation between BudgetScreen and BillsStatScreen
    NavHost(navController = navController, startDestination = "budget") {
        composable("budget") { BudgetScreen(navController) } // Pass NavController to BudgetScreen
        composable("bills_stat") { BillsStatScreen(navController) } // Pass NavController to BillsStatScreen
    }
}

@Composable
fun BudgetScreen(navController: NavController) {
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
        TopAppBar()

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BudgetTitle()
            BudgetInfoSection()
            BillsListSection(navController)
        }
    }
}

@Composable
fun TopAppBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* Handle home button click */ }) {
            Image(
                painter = painterResource(id = R.drawable.home), // Home icon
                contentDescription = "Home",
                modifier = Modifier.size(36.dp)
            )
        }

        Image(
            painter = painterResource(id = R.drawable.f), // F logo icon
            contentDescription = "F Logo",
            modifier = Modifier.size(36.dp)
        )

        IconButton(onClick = { /* Handle back button click */ }) {
            Image(
                painter = painterResource(id = R.drawable.back), // Back icon
                contentDescription = "Back",
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
fun BudgetTitle() {
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
fun BudgetInfoSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Remaining Budget", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold))
        Text("24,750 PHP", style = TextStyle(fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1)))

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Spent", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("75%", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(24.dp))

            Image(
                painter = painterResource(id = R.drawable.budget),
                contentDescription = "Budget Chart",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.width(24.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Remaining Budget", color = Color.Green, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("25%", color = Color.Green, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Total Income this Month: 99,000 PHP",
            style = TextStyle(fontSize = 18.sp, color = Color.Gray),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun BillsListSection(navController: NavController) {
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
            BillItem("Electricity:", "15,000 PHP", Modifier.weight(1f).clickable {
                navController.navigate("bills_stat")
            })
            BillItem("Water Bill:", "2,000 PHP", Modifier.weight(1f).clickable {
                navController.navigate("bills_stat")
            })
        }
    }
}

@Composable
fun BillItem(name: String, amount: String, modifier: Modifier = Modifier) {
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
fun PreviewMainScreen() {
    MainScreen()
}
