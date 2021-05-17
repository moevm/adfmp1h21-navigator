package com.example.androidnavigatorleti.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidnavigatorleti.R
import com.example.androidnavigatorleti.data.ParcelUserLocation
import com.example.androidnavigatorleti.data.TrafficLight
import com.example.androidnavigatorleti.data.UserLocation
import com.example.androidnavigatorleti.data.toLatLng
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.SphericalUtil
import com.google.maps.model.DirectionsResult
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class MapViewModel : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private val job = SupervisorJob()

    private var locationJob: Job? = null

    private val polylinePoints: ArrayList<LatLng> = ArrayList()
    private val trafficLights: ArrayList<TrafficLight> = ArrayList()

    private val polyLineMutableLiveData = MutableLiveData<PolylineOptions?>()
    val polyLineLiveData: LiveData<PolylineOptions?>
        get() = polyLineMutableLiveData

    private var currentDistance = 0.0
    private var currentSpeed = 0


    private lateinit var currentSpeedFlow: Flow<Int>
    private lateinit var currentDistanceFlow: Flow<Double>
    private lateinit var trafficLightFlow: Flow<Double>

    private lateinit var speedFlow: Flow<Pair<Int, Int>>

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

    fun collectFlows(lastLocation: UserLocation, newLocation: UserLocation) {
        launch(Dispatchers.IO) {
            currentDistanceFlow.collect {
                currentDistance -= SphericalUtil.computeDistanceBetween(
                    lastLocation.toLatLng(),
                    newLocation.toLatLng()
                )
                delay(1000)
            }

            trafficLightFlow.collect {
                if (it <= 0.0) trafficLights.removeAt(0)
                delay(1000)
            }

            currentSpeedFlow.collect {
                currentSpeed = SphericalUtil.computeDistanceBetween(
                    lastLocation.toLatLng(),
                    newLocation.toLatLng()
                ).toInt() / 1000 / 3600
                //speed_layout?.current_speed?.setText(it)
                delay(1000)
            }
        }
    }

    private fun makePolyline(
        geoApiContext: GeoApiContext,
        firstMarker: ParcelUserLocation?,
        secondMarker: ParcelUserLocation?
    ) {
        //Получаем контекст для запросов, mapsApiKey хранит в себе String с ключом для карт

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

        currentDistanceFlow = flow {
            emit(currentDistance)
        }

        trafficLightFlow = flow {
            emit(trafficLights[0].distance)
        }

        currentSpeedFlow = flow {
            emit(currentSpeed)
        }

        initTrafficLightList()
        currentDistance = getRootTrafficLightDistance()
    }

    private fun initTrafficLightList() {
        trafficLights.add(
            TrafficLight(
                location = LatLng(59.93296000000001, 30.244760000000003),
                distance = 0.0,
                orientation = 0,
                startGreenOffset = 0,
                startRedOffset = 10,
                interval = 20
            )
        )
        trafficLights.add(
            TrafficLight(
                location = LatLng(59.93401000000001, 30.247650000000004),
                distance = 0.0,
                orientation = 0,
                startGreenOffset = 5,
                startRedOffset = 10,
                interval = 23
            )
        )
        trafficLights.add(
            TrafficLight(
                location = LatLng(59.9346, 30.249350000000003),
                distance = 0.0,
                orientation = 0,
                startGreenOffset = 10,
                startRedOffset = 10,
                interval = 26
            )
        )
        trafficLights.add(
            TrafficLight(
                location = LatLng(59.939510000000006, 30.266050000000003),
                distance = 0.0,
                orientation = 0,
                startGreenOffset = 15,
                startRedOffset = 10,
                interval = 30
            )
        )
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
}