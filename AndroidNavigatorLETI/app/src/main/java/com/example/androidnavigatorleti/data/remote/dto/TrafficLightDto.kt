package com.example.androidnavigatorleti.data.remote.dto

import com.example.androidnavigatorleti.domain.model.TrafficLight
import com.google.android.gms.maps.model.LatLng

data class TrafficLightDto(
    val location: LatLng,
    val distance: Double,
    val orientation: Long,
    val startGreenOffset: Long,
    val startRedOffset: Long,
    val interval: Long
)

fun TrafficLightDto.toTrafficLight(): TrafficLight =
    TrafficLight(
        location = location,
        distance = distance,
        orientation = orientation,
        startGreenOffset = startGreenOffset,
        startRedOffset = startRedOffset,
        interval = interval
    )