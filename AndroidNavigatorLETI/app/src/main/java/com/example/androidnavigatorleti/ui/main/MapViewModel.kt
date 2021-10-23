package com.example.androidnavigatorleti.ui.main

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidnavigatorleti.R
import com.example.androidnavigatorleti.data.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.SphericalUtil
import com.google.maps.model.DirectionsResult
import com.instacart.library.truetime.TrueTime
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import java.io.IOException
import kotlin.coroutines.CoroutineContext
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class MapViewModel : ViewModel(), CoroutineScope {

    companion object {

        const val MAX_SPEED = 60L
        const val YELLOW_SIGNAL_TIME = 3L
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private val job = SupervisorJob()

    private var locationJob: Job? = null

    private val params = ViewStateParams()
    var isPolylineBuild = false

    var newLocation = UserLocation(lat = 0.0, lng = 0.0)
    var lastLocation: UserLocation? = null

    private val polylinePoints: ArrayList<LatLng> = ArrayList()
    val trafficLights: ArrayList<TrafficLight> = ArrayList()

    private val polyLineMutableLiveData = MutableLiveData<PolylineOptions?>()
    val polyLineLiveData: LiveData<PolylineOptions?>
        get() = polyLineMutableLiveData

    private val viewStateParamsMutableLiveData = MutableLiveData<ViewStateParams>()
    val viewStateParamsLiveData: LiveData<ViewStateParams>
        get() = viewStateParamsMutableLiveData

    private var paramsFlow = MutableStateFlow(ViewStateParams())

    fun checkJob(job: Job?) = job == null || job.isCompleted

    fun buildRoute(
            geoApiContext: GeoApiContext,
            firstMarker: ParcelUserLocation?,
            secondMarker: ParcelUserLocation?
    ) {
        if (checkJob(locationJob)) {
            locationJob = launch(this.coroutineContext) {
                withContext(Dispatchers.IO) {
                    makePolyline(geoApiContext, firstMarker, secondMarker)
                }
            }
        }
    }

    fun postFlowValues() {
        params.let {
            val delta = lastLocation?.let { last ->
                SphericalUtil.computeDistanceBetween(last.toLatLng(), newLocation.toLatLng())
            } ?: 0.0

            lastLocation = newLocation

            it.currentSpeed = (delta * 3.6).toInt()

            it.currentDistance = SphericalUtil.computeDistanceBetween(newLocation.toLatLng(), polylinePoints.last())
            trafficLights.forEach { light ->
                light.distance = SphericalUtil.computeDistanceBetween(newLocation.toLatLng(), light.location)
            }

            if (isPolylineBuild) {
                if (trafficLights.size > 0 && it.trafficLightDistance < trafficLights[0].distance) {
                    trafficLights.removeAt(0)
                }

                if (trafficLights.size > 0) it.trafficLightDistance = trafficLights[0].distance
            }

            val (minimumSpeed, maximumSpeed) = computeMinAndMaxSpeed()

            it.minSpeed = minimumSpeed
            it.maxSpeed = maximumSpeed

            viewStateParamsMutableLiveData.postValue(it)
        }
    }

    fun collectFlows() {
        viewModelScope.launch {
            paramsFlow.collect {
                //Log.d("HIHI", it.toString())
                viewStateParamsMutableLiveData.postValue(it)
            }
        }
    }

    private fun makePolyline(
            geoApiContext: GeoApiContext,
            firstMarker: ParcelUserLocation?,
            secondMarker: ParcelUserLocation?
    ) {
        isPolylineBuild = true

        //Здесь будет наш итоговый путь состоящий из набора точек
        var result: DirectionsResult? = null
        val startPoint =
                com.google.maps.model.LatLng(firstMarker?.lat ?: 0.0, firstMarker?.lng ?: 0.0)
        val endPoint =
                com.google.maps.model.LatLng(secondMarker?.lat ?: 0.0, secondMarker?.lng ?: 0.0)

        try {
            result = DirectionsApi.newRequest(geoApiContext)
                    .origin(startPoint)
                    .destination(endPoint)
                    .await()
        } catch (e: ApiException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        //Преобразование итогового пути в набор точек
        val path: MutableList<com.google.maps.model.LatLng>? =
                result?.routes?.getOrNull(0)?.overviewPolyline?.decodePath()
        //Линия которую будем рисовать
        val line = PolylineOptions()

        val latLngBuilder: LatLngBounds.Builder = LatLngBounds.Builder()

        //Проходимся по всем точкам, добавляем их в Polyline и в LanLngBounds.Builder
        if (path != null) {
            for (item in path) {
                val point = LatLng(item.lat, item.lng)
                polylinePoints.add(point)
                //Log.d("HIHI", point.toString())
                line.add(point)
                latLngBuilder.include(point)
            }
        }

        //Делаем линию более менее симпатичное
        line.width(16f).color(R.color.colorPrimary)

        polyLineMutableLiveData.postValue(line)

        initTrafficLightList()

        val curDistance = getRootTrafficLightDistance()
        val trafficLightDist = trafficLights[0].distance

        params.currentDistance = curDistance
        params.trafficLightDistance = trafficLightDist

        viewStateParamsMutableLiveData.postValue(params)

        val mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            override fun run() {
                postFlowValues()
                mainHandler.postDelayed(this, 1000)
            }
        })
    }

    fun initTrafficLightList() {
        trafficLights.add(
            TrafficLight(
                location = LatLng(59.93296000000001, 30.244760000000003),
                distance = 0.0,
                orientation = 0,
                startGreenOffset = 53,
                startRedOffset = 22,
                interval = 63
            )
        )
        trafficLights.add(
            TrafficLight(
                location = LatLng(59.93401000000001, 30.247650000000004),
                distance = 0.0,
                orientation = 0,
                startGreenOffset = 76,
                startRedOffset = 39,
                interval = 94
            )
        )
        trafficLights.add(
            TrafficLight(
                location = LatLng(59.9346, 30.249350000000003),
                distance = 0.0,
                orientation = 0,
                startGreenOffset = 40,
                startRedOffset = 61,
                interval = 69
            )
        )
        trafficLights.add(
            TrafficLight(
                location = LatLng(59.939510000000006, 30.266050000000003),
                distance = 0.0,
                orientation = 0,
                startGreenOffset = 17,
                startRedOffset = 71,
                interval = 95
            )
        )
//        trafficLights.add(
//                TrafficLight(
//                        location = LatLng(59.93296000000001, 30.244760000000003),
//                        distance = 0.0,
//                        orientation = 0,
//                        startGreenOffset = 17,
//                        startRedOffset = 50,
//                        interval = 60
//                )
//        )
//        trafficLights.add(
//                TrafficLight(
//                        location = LatLng(59.93401000000001, 30.247650000000004),
//                        distance = 0.0,
//                        orientation = 0,
//                        startGreenOffset = 51,
//                        startRedOffset = 13,
//                        interval = 95
//                )
//        )
//        trafficLights.add(
//                TrafficLight(
//                        location = LatLng(59.9346, 30.249350000000003),
//                        distance = 0.0,
//                        orientation = 0,
//                        startGreenOffset = 37,
//                        startRedOffset = 56,
//                        interval = 66
//                )
//        )
//        trafficLights.add(
//                TrafficLight(
//                        location = LatLng(59.939510000000006, 30.266050000000003),
//                        distance = 0.0,
//                        orientation = 0,
//                        startGreenOffset = 57,
//                        startRedOffset = 6,
//                        interval = 105
//                )
//        )
    }

    private fun getRootTrafficLightDistance(): Double {
        var lastPoint = polylinePoints.getOrNull(0) ?: return 0.0
        var sum = 0.0

        for (point in polylinePoints) {
            trafficLights.forEach {
                if (it.location.latitude in lastPoint.latitude..point.latitude
                        && it.location.longitude in lastPoint.longitude..point.longitude
                ) {
                    it.distance = sum + SphericalUtil.computeDistanceBetween(lastPoint, it.location)
                }
            }

            sum += SphericalUtil.computeDistanceBetween(lastPoint, point)
            lastPoint = point
        }

        return sum
    }

    private fun computeMinAndMaxSpeed(): Pair<Long, Long> {
        var redSpeed = 0L
        var greenSpeed = 60L

        trafficLights.getOrNull(0)?.apply {
            val currentOffset = TrueTime.now().time % interval
            val greenOffset = (startGreenOffset - currentOffset + interval) % interval
            val redOffset = (startRedOffset - currentOffset - YELLOW_SIGNAL_TIME + interval) % interval

            redSpeed = floor(distance / redOffset * 3.6).toLong()
            greenSpeed = floor(distance / greenOffset * 3.6).toLong()

            if (redSpeed > MAX_SPEED) {
                redSpeed = floor(distance / (redOffset + interval) * 3.6).toLong()
            }

            greenSpeed = min(greenSpeed, MAX_SPEED)
            redSpeed = min(redSpeed, MAX_SPEED)
        }

        return min(greenSpeed, redSpeed) to max(greenSpeed, redSpeed)
    }
}