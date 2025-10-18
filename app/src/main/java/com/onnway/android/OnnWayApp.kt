// app/src/main/java/com/onnway/android/OnnWayApp.kt
package com.onnway.android

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.LatLng
import com.onnway.android.data.models.*
import com.onnway.android.data.repository.RouteRepository
import com.onnway.android.ui.components.LoadingModal
import com.onnway.android.ui.screens.CitySelectionScreen
import com.onnway.android.ui.screens.RouteDisplayScreen
import com.onnway.android.ui.theme.*
import com.onnway.android.utils.LocationHelper
import kotlinx.coroutines.launch

@Composable
fun OnnWayApp() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { RouteRepository() }

    // UI State
    var isLoadingLocation by remember { mutableStateOf(true) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    // Form State
    var selectedCity by remember { mutableStateOf<City?>(null) }
    var selectedActivity by remember { mutableStateOf<ActivityType?>(null) }
    var selectedBudget by remember { mutableStateOf<BudgetRange?>(null) }
    var selectedDuration by remember { mutableStateOf<Duration?>(null) }

    // Route State
    var isCreatingRoute by remember { mutableStateOf(false) }
    var showLoadingModal by remember { mutableStateOf(false) }
    var route by remember { mutableStateOf<RouteResponse?>(null) }
    var attractions by remember { mutableStateOf<List<Attraction>>(emptyList()) }
    var routeError by remember { mutableStateOf<String?>(null) }

    // Get user location on start
    LaunchedEffect(Unit) {
        val location = LocationHelper.getCurrentLocation(context)
        if (location != null) {
            userLocation = LatLng(location.latitude, location.longitude)
            isLoadingLocation = false
        } else {
            // FAKE LOCATION for testing
            userLocation = LatLng(59.4370, 24.7536) // Tallinn
            isLoadingLocation = false
            Log.w("OnnWayApp", "Using fake location for testing")
        }
    }

    // Load attractions when needed
    LaunchedEffect(route) {
        if (route != null && attractions.isEmpty()) {
            coroutineScope.launch {
                repository.getAllAttractions().onSuccess { data ->
                    attractions = data
                }
            }
        }
    }

    // Main UI
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(GradientStart, GradientEnd)
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🇪🇪 Tere!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = White
                    )

                    Text(
                        text = "🗺️ OnnWay",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )

                    Text(
                        text = "Merhaba! 🇹🇷",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Create optimized tourism routes for Istanbul and Tallinn",
                    fontSize = 16.sp,
                    color = White,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(GradientStart, GradientEnd)
                    )
                )
        ) {
            when {
                // Loading location
                isLoadingLocation -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Getting your location...",
                            style = MaterialTheme.typography.headlineMedium,
                            color = White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Please allow location access for the app to work",
                            style = MaterialTheme.typography.bodyLarge,
                            color = White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Location error
                locationError != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "❌ Location Required",
                            style = MaterialTheme.typography.headlineMedium,
                            color = White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = locationError!!,
                            style = MaterialTheme.typography.bodyLarge,
                            color = White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Show route if created
                route != null && userLocation != null -> {
                    RouteDisplayScreen(
                        route = route!!,
                        attractions = attractions,
                        userLocation = userLocation!!,
                        onCreateNew = {
                            route = null
                            attractions = emptyList()
                            selectedCity = null
                            selectedActivity = null
                            selectedBudget = null
                            selectedDuration = null
                            routeError = null
                        }
                    )
                }

                // Show city selection
                userLocation != null -> {
                    CitySelectionScreen(
                        selectedCity = selectedCity,
                        selectedActivity = selectedActivity,
                        selectedBudget = selectedBudget,
                        selectedDuration = selectedDuration,
                        onCitySelect = { selectedCity = it },
                        onActivitySelect = { selectedActivity = it },
                        onBudgetSelect = { selectedBudget = it },
                        onDurationSelect = { selectedDuration = it },
                        onApply = {
                            if (userLocation != null &&
                                selectedCity != null &&
                                selectedActivity != null &&
                                selectedBudget != null &&
                                selectedDuration != null
                            ) {
                                showLoadingModal = true
                                isCreatingRoute = true
                                routeError = null

                                coroutineScope.launch {
                                    val request = RouteRequest(
                                        startLat = userLocation!!.latitude,
                                        startLon = userLocation!!.longitude,
                                        city = selectedCity!!,
                                        activity = selectedActivity!!,
                                        budget = selectedBudget!!,
                                        duration = selectedDuration!!
                                    )

                                    repository.createRoute(request)
                                        .onSuccess { response ->
                                            route = response
                                            showLoadingModal = false
                                            isCreatingRoute = false
                                        }
                                        .onFailure { error ->
                                            routeError = error.message ?: "Failed to create route"
                                            showLoadingModal = false
                                            isCreatingRoute = false
                                        }
                                }
                            }
                        },
                        isLoading = isCreatingRoute
                    )

                    // Show error if any
                    if (routeError != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            androidx.compose.material3.Card(
                                colors = androidx.compose.material3.CardDefaults.cardColors(
                                    containerColor = DangerColor
                                )
                            ) {
                                Text(
                                    text = "❌ $routeError",
                                    modifier = Modifier.padding(16.dp),
                                    color = White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Footer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGray)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Built with Kotlin and Jetpack Compose",
                fontSize = 14.sp,
                color = White.copy(alpha = 0.8f)
            )
        }
    }

    // Loading Modal
    LoadingModal(
        isVisible = showLoadingModal,
        onDismiss = {
            showLoadingModal = false
            isCreatingRoute = false
        }
    )
}