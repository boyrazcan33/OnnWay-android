// app/src/main/java/com/onnway/android/ui/screens/RouteDisplayScreen.kt
package com.onnway.android.ui.screens

import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.onnway.android.data.models.Attraction
import com.onnway.android.data.models.RouteResponse
import com.onnway.android.data.models.RouteStop
import com.onnway.android.ui.components.AttractionDetails
import com.onnway.android.ui.components.RouteMap
import com.onnway.android.ui.theme.*

@Composable
fun RouteDisplayScreen(
    route: RouteResponse,
    attractions: List<Attraction>,
    userLocation: LatLng,
    onCreateNew: () -> Unit
) {
    var selectedStop by remember { mutableStateOf<RouteStop?>(null) }
    var selectedAttraction by remember { mutableStateOf<Attraction?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Route Summary
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🗺️ Your Optimized Route",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SummaryItem(
                            label = "Total Distance",
                            value = route.totalDistance
                        )
                        SummaryItem(
                            label = "Total Duration",
                            value = route.totalDuration
                        )
                        SummaryItem(
                            label = "Total Cost",
                            value = "€${route.totalCost}"
                        )
                    }
                }
            }
        }

        // Route Map
        item {
            RouteMap(
                route = route.optimizedRoute,
                attractions = attractions,
                userLocation = userLocation,
                onAttractionClick = { stop ->
                    selectedStop = stop
                    selectedAttraction = attractions.find { it.name == stop.name }
                }
            )
        }

        // Route Steps Header
        item {
            Text(
                text = "Route Steps",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Route Steps
        items(route.optimizedRoute) { stop ->
            RouteStepCard(
                stop = stop,
                onClick = {
                    selectedStop = stop
                    selectedAttraction = attractions.find { it.name == stop.name }
                }
            )
        }

        // Create New Route Button
        item {
            Button(
                onClick = onCreateNew,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryColor
                )
            ) {
                Text(
                    text = "Create New Route",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Attraction Details Dialog
    if (selectedStop != null) {
        AttractionDetails(
            stop = selectedStop!!,
            attraction = selectedAttraction,
            onDismiss = {
                selectedStop = null
                selectedAttraction = null
            }
        )
    }
}

@Composable
fun SummaryItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor
        )
    }
}

@Composable
fun RouteStepCard(
    stop: RouteStop,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Step Number
            Surface(
                modifier = Modifier.size(40.dp),
                shape = MaterialTheme.shapes.small,
                color = PrimaryColor
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${stop.order}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = stop.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TagChip(
                            text = "🚶 ${stop.walkingTime}",
                            backgroundColor = PrimaryColor.copy(alpha = 0.1f),
                            textColor = PrimaryColor
                        )
                        TagChip(
                            text = "💰 €${stop.cost}",
                            backgroundColor = SuccessColor.copy(alpha = 0.1f),
                            textColor = SuccessColor
                        )
                        TagChip(
                            text = "⏱️ ${stop.estimatedDuration} min",
                            backgroundColor = WarningColor.copy(alpha = 0.1f),
                            textColor = DarkGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "📍 ${stop.address}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stop.description,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = onClick) {
                    Text("View Details →")
                }
            }
        }
    }
}

@Composable
fun TagChip(
    text: String,
    backgroundColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = backgroundColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}