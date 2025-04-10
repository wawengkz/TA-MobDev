package com.example.familyflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.familyflow.ui.theme.FamilyFlowTheme

class BudgetActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FamilyFlowTheme {
                val navController = rememberNavController()

                // Define NavHost for handling navigation between BudgetScreen and BillsStatScreen
                NavHost(navController = navController, startDestination = "budget") {
                    composable("budget") { BudgetScreen(navController) } // Pass navController to BudgetScreen
                    composable("bills_stat") { BillsStatScreen(navController) } // Pass navController to BillsStatScreen
                }
            }
        }
    }
}
