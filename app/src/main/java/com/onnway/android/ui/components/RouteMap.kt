// app/src/main/java/com/onnway/android/ui/components/RouteMap.kt
package com.onnway.android.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import com.onnway.android.data.models.Attraction
import com.onnway.android.data.models.RouteStop

@Composable
fun RouteMap(
    route: List<RouteStop>,
    attractions: List<Attraction>,
    userLocation: LatLng,
    onAttractionClick: (RouteStop) -> Unit = {}
) {
    val context = LocalContext.current

    // Match route stops with attractions for coordinates
    val enhancedRoute = remember(route, attractions) {
        route.mapNotNull { stop ->
            val attraction = attractions.find { it.name == stop.name }
            attraction?.let { stop to it }
        }
    }

    // Calculate bounds for all points
    val bounds = remember(enhancedRoute, userLocation) {
        val builder = LatLngBounds.Builder()
        builder.include(userLocation)
        enhancedRoute.forEach { (_, attraction) ->
            builder.include(LatLng(attraction.latitude, attraction.longitude))
        }
        builder.build()
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, 13f)
    }

    LaunchedEffect(bounds) {
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngBounds(bounds, 100)
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📍 Route Map",
                style = MaterialTheme.typography.headlineMedium
            )

            Button(
                onClick = {
                    val coordinates = buildString {
                        append("${userLocation.latitude},${userLocation.longitude}")
                        enhancedRoute.forEach { (_, attraction) ->
                            append(";${attraction.latitude},${attraction.longitude}")
                        }
                    }
                    val uri = Uri.parse("https://www.openstreetmap.org/directions?engine=fossgis_osrm_foot&route=$coordinates")
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
            ) {
                Text("Open in Maps")
            }
        }

        // Map
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = false)
            ) {
                // User location marker
                Marker(
                    state = MarkerState(position = userLocation),
                    title = "Your Location",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )

                // Route markers
                enhancedRoute.forEach { (stop, attraction) ->
                    Marker(
                        state = MarkerState(
                            position = LatLng(attraction.latitude, attraction.longitude)
                        ),
                        title = stop.name,
                        snippet = stop.address,
                        onClick = {
                            onAttractionClick(stop)
                            true
                        }
                    )
                }

                // Route polyline
                if (enhancedRoute.isNotEmpty()) {
                    val routePoints = listOf(userLocation) + enhancedRoute.map {
                        LatLng(it.second.latitude, it.second.longitude)
                    }
                    Polyline(
                        points = routePoints,
                        color = androidx.compose.ui.graphics.Color(0xFF007BFF),
                        width = 8f
                    )
                }
            }
        }
    }
}