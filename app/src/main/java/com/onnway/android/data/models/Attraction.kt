// app/src/main/java/com/onnway/android/data/models/Attraction.kt
package com.onnway.android.data.models

data class Attraction(
    val id: Long,
    val name: String,
    val address: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val city: String,
    val activityType: String,
    val budgetRange: String,
    val estimatedDuration: Int,
    val avgCost: Double
)