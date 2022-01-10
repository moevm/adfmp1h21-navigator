package com.example.androidnavigatorleti.data.remote

import com.example.androidnavigatorleti.data.remote.dto.TrafficLightDto
import retrofit2.http.GET

interface TrafficLightApi {

    @GET("/")
    suspend fun getTrafficLights(): List<TrafficLightDto>
}