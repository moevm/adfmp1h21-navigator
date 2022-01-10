package com.example.androidnavigatorleti.domain.repository

import com.example.androidnavigatorleti.data.remote.dto.TrafficLightDto

interface TrafficLightsRepository {

    suspend fun getTrafficLights(): List<TrafficLightDto>
}