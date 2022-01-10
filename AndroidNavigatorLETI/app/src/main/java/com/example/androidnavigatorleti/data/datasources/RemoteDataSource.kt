package com.example.androidnavigatorleti.data.datasources

import com.example.androidnavigatorleti.data.remote.TrafficLightApi
import com.example.androidnavigatorleti.data.remote.dto.TrafficLightDto

class RemoteDataSource(
    private val service: TrafficLightApi
) {

    suspend fun getTrafficLights(): List<TrafficLightDto> = service.getTrafficLights()
}