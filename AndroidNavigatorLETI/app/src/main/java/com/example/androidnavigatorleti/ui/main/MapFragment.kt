package com.example.androidnavigatorleti.ui.main

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.androidnavigatorleti.R
import androidx.navigation.fragment.navArgs
import com.example.androidnavigatorleti.*
import com.example.androidnavigatorleti.base.BaseFragment
import com.example.androidnavigatorleti.data.ParcelUserLocation
import com.example.androidnavigatorleti.data.UserLocation
import com.example.androidnavigatorleti.preferences.SharedPreferencesManager.Keys.LAT_KEY
import com.example.androidnavigatorleti.preferences.SharedPreferencesManager.Keys.LNG_KEY
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.SphericalUtil
import com.google.maps.model.DirectionsResult
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.coroutines.*
import java.io.IOException
import kotlin.coroutines.CoroutineContext
import kotlin.math.floor


class MapFragment : BaseFragment(), CoroutineScope, LocationListener {

    companion object {

        private const val DEFAULT_ZOOM = 12f
        private const val ZOOM_SPEED = 400
        private const val DEFAULT_USER_LATITUDE = 30.315492
        private const val DEFAULT_USER_LONGITUDE = 59.939007
    }

    private val args: MapFragmentArgs by navArgs()
    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private var map: GoogleMap? = null
    private var firstMarker: Marker? = null
    private var secondMarker: Marker? = null
    private var permissionGranted = false
    private var locationJob: Job? = null
    private var polyLine: Polyline? = null

    private lateinit var lm: LocationManager
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionGranted = requireContext().checkLocationPermission()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        lm = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_map, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        map_view.onCreate(savedInstanceState)
        showMap()

        if (permissionGranted) startLocationUpdates()

        if (args.makeRoot) {
            close_floating_button.visibility = View.VISIBLE
            search_container.visibility = View.VISIBLE
            my_location_floating_button.visibility = View.GONE

            if (checkJob(locationJob)) {
                locationJob = launch(this.coroutineContext) {
                    withContext(Dispatchers.IO) {
                        makePolyline()
                    }
                }
            }

            close_floating_button.setOnClickListener {
                close_floating_button.visibility = View.GONE
                search_container.visibility = View.GONE
                my_location_floating_button.visibility = View.VISIBLE
                removeMarkers()
                polyLine?.remove()
            }
        }

        if (args.setFirstMarker) {
            root_button_page.visibility = View.VISIBLE
            my_location_floating_button.visibility = View.GONE
            my_location_button.setOnClickListener {
                firstMarker?.remove()
                showMarkerLocation()
            }
            choose_button.setOnClickListener {
                val markerLocation: LatLng? = firstMarker?.position
                val direction = MapFragmentDirections.actionFirstMarkerSet(ParcelUserLocation(
                    markerLocation!!.latitude,
                    markerLocation.longitude
                ))
                openFragment(direction)
            }
        }

        if (args.setSecondMarker) {
            root_button_page.visibility = View.VISIBLE
            my_location_floating_button.visibility = View.GONE
            my_location_button.setOnClickListener {
                secondMarker?.remove()
                showMarkerLocation()
            }
            choose_button.setOnClickListener {
                val firstMarkerLocation: LatLng? = firstMarker?.position
                val secondMarkerLocation: LatLng? = secondMarker?.position
                val direction = MapFragmentDirections.actionAllMarkersSet(
                    ParcelUserLocation(firstMarkerLocation!!.latitude, firstMarkerLocation.longitude),
                    ParcelUserLocation(secondMarkerLocation!!.latitude, secondMarkerLocation.longitude)
                )
                openFragment(direction)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        map_view.onStart()
    }

    override fun onResume() {
        super.onResume()

        map_view.onResume()
        my_location_floating_button.setOnClickListener {
            val newLocation = NavigatorApp.userDao.getLocation()
            showLocation(newLocation, permissionGranted, true)
        }
    }

    override fun onPause() {
        map_view.onPause()

        super.onPause()
    }

    override fun onStop() {
        map_view.onStop()

        super.onStop()
    }

    override fun onDestroy() {
        map_view?.onDestroy()

        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                permissionGranted = requireContext().checkLocationPermission()

                if (permissionGranted) {
                    startLocationUpdates()
                    map?.isMyLocationEnabled = true
                }
            }
        }
    }

    override fun onLocationChanged(p0: Location) {
        val newLocation = UserLocation(0, p0.latitude, p0.longitude)
        saveUserLocation(newLocation)
    }

    private suspend fun makePolyline() {
        //Получаем контекст для запросов, mapsApiKey хранит в себе String с ключом для карт
        val geoApiContext = GeoApiContext().setApiKey(getString(R.string.google_maps_key))

        //Здесь будет наш итоговый путь состоящий из набора точек
        var result: DirectionsResult? = null
        val startPoint = com.google.maps.model.LatLng(args.firstMarker!!.lat, args.firstMarker!!.lng)
        val endPoint = com.google.maps.model.LatLng(args.secondMarker!!.lat, args.secondMarker!!.lng)

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
        val path: MutableList<com.google.maps.model.LatLng>? = result!!.routes[0].overviewPolyline.decodePath()
        //Линия которую будем рисовать
        val line = PolylineOptions()

        val latLngBuilder: LatLngBounds.Builder = LatLngBounds.Builder()

        //Проходимся по всем точкам, добавляем их в Polyline и в LanLngBounds.Builder
        if (path != null) {
            for (item in path) {
                line.add(LatLng(item.lat, item.lng))
                latLngBuilder.include(LatLng(item.lat, item.lng))
            }
        }

        //Делаем линию более менее симпатичное
        line.width(16f).color(R.color.colorPrimary)

        //Добавляем линию на карту
        withContext(Dispatchers.Main) {
            polyLine = map?.addPolyline(line)
            showDoubleBlockedMarkers()
            route_time?.text = countDistance(startPoint, endPoint)
        }
    }

    private fun countDistance(startPoint: com.google.maps.model.LatLng, endPoint: com.google.maps.model.LatLng): String {
        val distance = SphericalUtil.computeDistanceBetween(
            LatLng(startPoint.lat, startPoint.lng),
            LatLng(endPoint.lat, endPoint.lng)
        )

        val time = floor(distance / 1000 * 2.5).toInt()

        return if (time >= 60) {
            val hours = time / 60
            val minutes = time % 60
            getString(R.string.route_time_text_hours, hours.toString(), minutes.toString())
        } else {
            getString(R.string.route_time_text_min, time.toString())
        }
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 100
        locationRequest.fastestInterval = 100
        locationRequest.smallestDisplacement = 1f
    }

    private fun startLocationUpdates() {
        buildLocationRequest()
        buildLocationCallBack()

        mFusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    //Build the location callback object and obtain the location results //as demonstrated below:
    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    saveUserLocation(UserLocation(lat = latitude, lng = longitude))
                }
            }
        }
    }

    private fun showMap() {
        map_view.getMapAsync { googleMap ->
            map = googleMap.apply {
                //Show user location with delay
                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    showMarkerLocation()
                },500)

                uiSettings.isMyLocationButtonEnabled = false
                uiSettings.isMapToolbarEnabled = false

                setOnMapLoadedCallback {
                    loading_page?.visibility = View.GONE
                    map_view?.visibility = View.VISIBLE
                }



                val geocoder = Geocoder(requireActivity())

                //Log.d("HIHI", geocoder.getFromLocationName("21-я лин. В.О., 010Г", 1).toString())

                if (!args.setFirstMarker && !args.setSecondMarker) {
                    setOnMapLongClickListener {
                        val direction = MapFragmentDirections.actionMapLongClick(ParcelUserLocation(it.latitude, it.longitude))
                        openFragment(direction)
//                    val loc = geocoder.getFromLocation(it.latitude, it.longitude, 1)
//                    val direction = MapFragmentDirections.actionMapLongClick(loc[0].getAddressLine(0).toString())
//
//                    val test = NavigatorApp.userDao.getLocation()
//                    val res = requestDirection(getRequestedUrl(it, LatLng(test.lat, test.lng)) ?: "")
//                    Log.d("HIHI", "$res abc")

                        //Log.d("HIHI", geocoder.getFromLocationName(loc[0].getAddressLine(0).toString(), 1).toString())
                    }
                }
            }

            if (permissionGranted) {
                map?.isMyLocationEnabled = true
            } else {
                requestLocationPermissions()
            }
        }
    }

    private fun showMarkerLocation() {
        val userLoc = NavigatorApp.userDao.getLocation()
        showLocation(userLoc, permissionGranted, true)

        if (args.setFirstMarker) {
            firstMarker = map?.addMarker(
                MarkerOptions().position(LatLng(userLoc.lat, userLoc.lng)).draggable(true)
            )

            map?.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
                override fun onMarkerDragEnd(p0: Marker?) {
                    firstMarker?.position = p0?.position
                }

                override fun onMarkerDragStart(p0: Marker?) {}

                override fun onMarkerDrag(p0: Marker?) {}
            })
        }

        if (args.setSecondMarker) {
            firstMarker = map?.addMarker(
                MarkerOptions().position(LatLng(args.secondMarker!!.lat, args.secondMarker!!.lng)).draggable(false)
            )

            secondMarker = map?.addMarker(
                MarkerOptions()
                    .position(LatLng(userLoc.lat, userLoc.lng))
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )

            map?.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
                override fun onMarkerDragEnd(p0: Marker?) {
                    secondMarker?.position = p0?.position
                }

                override fun onMarkerDragStart(p0: Marker?) {}

                override fun onMarkerDrag(p0: Marker?) {}
            })
        }
    }

    private fun showDoubleBlockedMarkers() {
        firstMarker = map?.addMarker(
            MarkerOptions().
                position(LatLng(args.firstMarker!!.lat, args.firstMarker!!.lng)).
                draggable(false)
        )

        secondMarker = map?.addMarker(
            MarkerOptions()
                .position(LatLng(args.secondMarker!!.lat, args.secondMarker!!.lng))
                .draggable(false)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        )
    }

    private fun removeMarkers() {
        firstMarker?.remove()
        secondMarker?.remove()
    }

    private fun saveUserLocation(location: UserLocation) {
        NavigatorApp.userDao.insertLocation(location)
        prefsManager.putDouble(LAT_KEY, location.lat)
        prefsManager.putDouble(LNG_KEY, location.lng)
    }

    private fun showLocation(
            location: UserLocation?,
            permissionIsGranted: Boolean,
            resetZoom: Boolean = true
    ) {
        if (location != null && permissionIsGranted && lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            showDeviceLocation(location, resetZoom)
        } else {
            getDefaultDeviceLocation()
        }
    }

    private fun getDefaultDeviceLocation() {
        val defaultLatLng = UserLocation(lat = DEFAULT_USER_LATITUDE, lng = DEFAULT_USER_LONGITUDE)
        showDeviceLocation(UserLocation(lat = defaultLatLng.lat, lng = defaultLatLng.lng), true)
    }

    private fun showDeviceLocation(location: UserLocation?, resetZoom: Boolean) {
        map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(LatLng(location!!.lat, location.lng),
                        if (resetZoom) DEFAULT_ZOOM else map?.cameraPosition?.zoom ?: DEFAULT_ZOOM),
                ZOOM_SPEED,
                null)
    }
}