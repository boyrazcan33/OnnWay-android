// app/src/main/java/com/onnway/android/data/models/RouteRequest.kt
package com.onnway.android.data.models

data class RouteRequest(
    val startLat: Double,
    val startLon: Double,
    val city: City,
    val activity: ActivityType,
    val budget: BudgetRange,
    val duration: Duration
)