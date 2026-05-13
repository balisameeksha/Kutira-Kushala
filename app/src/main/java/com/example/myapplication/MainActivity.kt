package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*

/* ---------------- DATA MODEL ---------------- */

data class Business(
    val name: String,
    val skill: String,
    val location: String
)

/* ---------------- MAIN ACTIVITY ---------------- */

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {
            MyApp()
        }
    }
}

/* ---------------- NAVIGATION ---------------- */

@Composable
fun MyApp() {

    val navController = rememberNavController()

    val businessList = remember {

        mutableStateListOf(

            Business("Food Corner", "Food", "Bangalore"),
            Business("Craft World", "Craft", "Mysore"),
            Business("Tech Services", "Services", "Hubli")
        )
    }

    Scaffold(

        bottomBar = {

            NavigationBar {

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("home")
                    },
                    icon = {
                        Icon(Icons.Default.Home, contentDescription = null)
                    },
                    label = {
                        Text("Home")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("analytics")
                    },
                    icon = {
                        Icon(Icons.Default.Info, contentDescription = null)
                    },
                    label = {
                        Text("Analytics")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("add_business")
                    },
                    icon = {
                        Icon(Icons.Default.Add, contentDescription = null)
                    },
                    label = {
                        Text("Add")
                    }
                )
            }
        }

    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {

            composable("home") {

                KutiraHomeScreen(
                    navController,
                    businessList
                )
            }

            composable("add_business") {

                AddBusinessScreen(
                    navController,
                    businessList
                )
            }

            composable("analytics") {

                AnalyticsScreen(businessList)
            }

            composable(
                "details/{name}/{skill}/{location}"
            ) { backStackEntry ->

                val name =
                    backStackEntry.arguments?.getString("name") ?: ""

                val skill =
                    backStackEntry.arguments?.getString("skill") ?: ""

                val location =
                    backStackEntry.arguments?.getString("location") ?: ""

                BusinessDetailScreen(
                    name,
                    skill,
                    location
                )
            }
        }
    }
}

/* ---------------- HOME SCREEN ---------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KutiraHomeScreen(
    navController: NavHostController,
    businessList: List<Business>
) {

    var searchText by remember {
        mutableStateOf("")
    }

    val categories =
        listOf("All", "Food", "Craft", "Services")

    var selectedCategory by remember {
        mutableStateOf("All")
    }

    val filteredList = businessList.filter {

        (selectedCategory == "All"
                || it.skill == selectedCategory)

                &&

                (
                        it.name.contains(searchText, true)
                                ||
                                it.location.contains(searchText, true)
                        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFEEF2FF),
                        Color(0xFFDDE5FF)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text(
                "Kutira Kushala",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5A4FCF)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF5A4FCF)
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        "Welcome 👋",
                        color = Color.White
                    )

                    Text(
                        "Smart Business Discovery Platform",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                },
                label = {
                    Text("Search Business")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow {

                items(categories) { category ->

                    FilterChip(
                        selected =
                            selectedCategory == category,

                        onClick = {
                            selectedCategory = category
                        },

                        label = {
                            Text(category)
                        },

                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredList.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        "No Businesses Found",
                        fontSize = 20.sp
                    )
                }

            } else {

                LazyColumn {

                    items(filteredList) { business ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {

                                    navController.navigate(
                                        "details/${business.name}/${business.skill}/${business.location}"
                                    )
                                },

                            elevation =
                                CardDefaults.cardElevation(6.dp),

                            colors =
                                CardDefaults.cardColors(
                                    containerColor = Color.White
                                )
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {

                                Text(
                                    business.name,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(
                                    modifier = Modifier.height(6.dp)
                                )

                                Text("🛠 ${business.skill}")
                                Text("📍 ${business.location}")
                            }
                        }
                    }
                }
            }
        }
    }
}

/* ---------------- ADD BUSINESS SCREEN ---------------- */

@Composable
fun AddBusinessScreen(
    navController: NavHostController,
    businessList: MutableList<Business>
) {

    var name by remember {
        mutableStateOf("")
    }

    var skill by remember {
        mutableStateOf("")
    }

    var location by remember {
        mutableStateOf("")
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFEEF2FF),
                        Color(0xFFDDE5FF)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text(
                "Add Business",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5A4FCF)
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                },
                label = {
                    Text("Business Name")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = skill,
                onValueChange = {
                    skill = it
                },
                label = {
                    Text("Category")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = location,
                onValueChange = {
                    location = it
                },
                label = {
                    Text("Location")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (errorMessage.isNotEmpty()) {

                Text(
                    errorMessage,
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {

                    if (
                        name.isEmpty()
                        || skill.isEmpty()
                        || location.isEmpty()
                    ) {

                        errorMessage =
                            "Please fill all fields"

                    } else {

                        businessList.add(
                            Business(
                                name,
                                skill,
                                location
                            )
                        )

                        navController.popBackStack()
                    }
                },

                modifier = Modifier.fillMaxWidth()
            ) {

                Text("Save Business")
            }
        }
    }
}

/* ---------------- ANALYTICS SCREEN ---------------- */

@Composable
fun AnalyticsScreen(
    businessList: List<Business>
) {

    val totalBusinesses = businessList.size

    val foodCount =
        businessList.count { it.skill == "Food" }

    val craftCount =
        businessList.count { it.skill == "Craft" }

    val serviceCount =
        businessList.count { it.skill == "Services" }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFEEF2FF),
                        Color(0xFFDDE5FF)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text(
                "Business Analytics",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5A4FCF)
            )

            Spacer(modifier = Modifier.height(20.dp))

            AnalyticsCard(
                "Total Businesses",
                totalBusinesses.toString()
            )

            AnalyticsCard(
                "Food Businesses",
                foodCount.toString()
            )

            AnalyticsCard(
                "Craft Businesses",
                craftCount.toString()
            )

            AnalyticsCard(
                "Service Businesses",
                serviceCount.toString()
            )
        }
    }
}

/* ---------------- ANALYTICS CARD ---------------- */

@Composable
fun AnalyticsCard(
    title: String,
    value: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),

        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                title,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5A4FCF)
            )
        }
    }
}

/* ---------------- DETAIL SCREEN ---------------- */

@Composable
fun BusinessDetailScreen(
    name: String,
    skill: String,
    location: String
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFEEF2FF),
                        Color(0xFFDDE5FF)
                    )
                )
            )
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.Center),

            elevation =
                CardDefaults.cardElevation(8.dp)
        ) {

            Column(
                modifier = Modifier.padding(24.dp)
            ) {

                Text(
                    name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(
                    "Category: $skill",
                    fontSize = 20.sp
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    "Location: $location",
                    fontSize = 20.sp
                )
            }
        }
    }
}