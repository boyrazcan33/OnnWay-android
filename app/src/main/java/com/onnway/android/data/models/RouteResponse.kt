// app/src/main/java/com/onnway/android/data/models/RouteResponse.kt
package com.onnway.android.data.models

data class RouteResponse(
    val optimizedRoute: List<RouteStop>,
    val totalDistance: String,
    val totalDuration: String,
    val totalCost: Double
)

data class RouteStop(
    val order: Int,
    val name: String,
    val address: String,
    val description: String,
    val estimatedDuration: Int,
    val walkingTime: String,
    val cost: Double
)