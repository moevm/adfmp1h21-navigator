package com.example.androidnavigatorleti.domain.use_case

import com.example.androidnavigatorleti.common.Resource
import com.example.androidnavigatorleti.domain.model.TrafficLight
import com.example.androidnavigatorleti.data.remote.dto.toTrafficLight
import com.example.androidnavigatorleti.domain.repository.TrafficLightsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class TrafficLightsUseCase(
    private val repository: TrafficLightsRepository
) {
    operator fun invoke(): Flow<Resource<List<TrafficLight>>> = flow {
        try {
            emit(Resource.Loading())
            val trafficLights = repository.getTrafficLights().map {
                it.toTrafficLight()
            }
            emit(Resource.Success(trafficLights))
        } catch(e: HttpException) {
            emit(Resource.Error<List<TrafficLight>>(e.localizedMessage ?: "An unexpected error occured"))
        } catch(e: IOException) {
            emit(Resource.Error<List<TrafficLight>>("Couldn't reach server. Check your internet connection."))
        }
    }
}