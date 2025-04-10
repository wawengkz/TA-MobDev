package com.example.familyflow

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.familyflow.ui.theme.FamilyFlowTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

data class Event(val date: String, val title: String)

class CalendarEvent : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FamilyFlowTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "calendar") {
                    composable("calendar") {
                        GradientBackgroundCalendarScreen(
                            onDateSelected = { date ->
                                val formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                                navController.navigate("addEvent/$formattedDate")
                            },
                            navController = navController // Pass navController to handle back navigation
                        )
                    }
                    composable(
                        route = "addEvent/{date}",
                        arguments = listOf(navArgument("date") {
                            type = NavType.StringType
                        })
                    ) { backStackEntry ->
                        val dateString = backStackEntry.arguments?.getString("date")
                        val selectedDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
                        AddEventScreen(
                            selectedDate = selectedDate,
                            onBackClick = { navController.popBackStack() }, // Handle going back
                            onHomeClick = {
                                navController.popBackStack("calendar", false) // Navigate back to the main calendar
                            },
                            onEventAdded = { /* Handle event addition logic */ }
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GradientBackgroundCalendarScreen(
    onDateSelected: (LocalDate) -> Unit,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF4FC3F7), Color(0xFF039BE5))
                )
            )
            .padding(16.dp)
    ) {
        EventCalendarScreen(
            onDateSelected = onDateSelected,
            onBackClick = { navController.popBackStack() }, // Pass navigation function
            onHomeClick = {
                navController.popBackStack("calendar", inclusive = false)
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventCalendarScreen(
    onDateSelected: (LocalDate) -> Unit,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    val events = remember { mutableStateOf(listOf(Event("14", "Mom's Birthday"), Event("23", "House Blessing"), Event("30", "Holiday"))) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(onBackClick = onBackClick, onHomeClick = onHomeClick)

        Text(
            text = "Event Calendar",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        CustomCalendarView(
            onDateSelected = onDateSelected,
            highlightedDates = events.value.map { it.date.toInt() }
        )

        Text(
            text = "Upcoming Events",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(top = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(events.value) { event ->
                EventItem(event)
            }
        }
    }
}

@Composable
private fun TopBar(onBackClick: () -> Unit, onHomeClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Home icon
        IconButton(onClick = onHomeClick) {
            Image(
                painter = painterResource(id = R.drawable.home),
                contentDescription = "Home",
                modifier = Modifier.size(40.dp)
            )
        }

        // Logo in the center
        Image(
            painter = painterResource(id = R.drawable.f),
            contentDescription = "Logo",
            modifier = Modifier.size(40.dp)
        )

        // Back icon
        IconButton(onClick = onBackClick) {
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back",
                modifier = Modifier.size(40.dp)
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomCalendarView(onDateSelected: (LocalDate) -> Unit, highlightedDates: List<Int>) {
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    val daysInMonth = currentYearMonth.lengthOfMonth()
    val firstDayOfWeek = currentYearMonth.atDay(1).dayOfWeek.value % 7

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { currentYearMonth = currentYearMonth.minusMonths(1) }) {
                Text("<", fontSize = 18.sp, color = Color.Black)
            }
            Text(
                text = currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            IconButton(onClick = { currentYearMonth = currentYearMonth.plusMonths(1) }) {
                Text(">", fontSize = 18.sp, color = Color.Black)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT").forEach { day ->
                Text(text = day, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
            }
        }

        var day = 1 - firstDayOfWeek
        for (week in 0 until 6) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (column in 0 until 7) {
                    if (day in 1..daysInMonth) {
                        val selectedDate = LocalDate.of(currentYearMonth.year, currentYearMonth.month, day)
                        val isHighlighted = highlightedDates.contains(day)
                        Text(
                            text = day.toString(),
                            fontSize = 16.sp,
                            color = if (isHighlighted) Color(0xFF1976D2) else Color.Black,
                            modifier = Modifier
                                .clickable { onDateSelected(selectedDate) }
                                .padding(8.dp),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Spacer(modifier = Modifier.size(32.dp))
                    }
                    day++
                }
            }
        }
    }
}

@Composable
fun EventItem(event: Event) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF4FC3F7), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = event.date,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = event.title,
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun EventCalendarScreenPreview() {
    FamilyFlowTheme {
        val navController = rememberNavController() // Create a mock NavController for preview
        GradientBackgroundCalendarScreen(
            onDateSelected = {},
            navController = navController // Pass the mock NavController
        )
    }
}
