package com.example.androidnavigatorleti.data.domain

import com.google.android.gms.maps.model.LatLng

data class TrafficLight(
        val location: LatLng,
        var distance: Double,
        val orientation: Long,
        val startGreenOffset: Long,
        val startRedOffset: Long,
        val interval: Long
)