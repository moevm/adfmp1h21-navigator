package com.example.androidnavigatorleti.data.repository

import com.example.androidnavigatorleti.data.datasources.LocalDataSource
import com.example.androidnavigatorleti.data.datasources.RemoteDataSource
import com.example.androidnavigatorleti.data.remote.dto.TrafficLightDto
import com.example.androidnavigatorleti.domain.repository.TrafficLightsRepository

class TrafficLightsRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
): TrafficLightsRepository {

    override suspend fun getTrafficLights(): List<TrafficLightDto> {
        return remoteDataSource.getTrafficLights()
    }
}