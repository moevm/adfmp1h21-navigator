package com.example.androidnavigatorleti.data

data class ViewStateParams(
    var minSpeed: Int = 0,
    var maxSpeed: Int = 0,
    var currentSpeed: Int = 0,
    var currentDistance: Double = 0.0,
    var trafficLightDistance: Double = 0.0
)