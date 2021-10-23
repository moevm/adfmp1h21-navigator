package com.example.androidnavigatorleti.data

import com.google.android.gms.maps.model.LatLng

data class TrafficLight(
        var location: LatLng,
        var distance: Double,
        var orientation: Long,
        var startGreenOffset: Long,
        var startRedOffset: Long,
        var interval: Long
)