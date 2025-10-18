// app/src/main/java/com/onnway/android/data/api/ApiService.kt
package com.onnway.android.data.api

import com.onnway.android.data.models.Attraction
import com.onnway.android.data.models.RouteRequest
import com.onnway.android.data.models.RouteResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("route/create")
    suspend fun createRoute(@Body request: RouteRequest): RouteResponse

    @GET("attractions")
    suspend fun getAttractions(
        @Query("city") city: String
    ): List<Attraction>
}