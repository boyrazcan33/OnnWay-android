// app/src/main/java/com/onnway/android/data/repository/RouteRepository.kt
package com.onnway.android.data.repository

import android.util.Log
import com.onnway.android.data.api.RetrofitClient
import com.onnway.android.data.models.Attraction
import com.onnway.android.data.models.RouteRequest
import com.onnway.android.data.models.RouteResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RouteRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun createRoute(request: RouteRequest): Result<RouteResponse> {
        return withContext(Dispatchers.IO) {
            val start = System.currentTimeMillis()
            try {
                val response = apiService.createRoute(request)
                val duration = System.currentTimeMillis() - start
                Log.d("API_PERF", "SUCCESS: ${duration}ms")
                Result.success(response)
            } catch (e: Exception) {
                val duration = System.currentTimeMillis() - start
                Log.e("API_PERF", "FAILED: ${duration}ms - ${e.message}")
                Result.failure(e)
            }
        }
    }

    suspend fun getAllAttractions(): Result<List<Attraction>> {
        return withContext(Dispatchers.IO) {
            try {
                val tallinnAttractions = apiService.getAttractions("TALLINN")
                val istanbulAttractions = apiService.getAttractions("ISTANBUL")
                val allAttractions = tallinnAttractions + istanbulAttractions
                Result.success(allAttractions)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}