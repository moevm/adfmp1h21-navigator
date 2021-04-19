package com.example.androidnavigatorleti.ui.main

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    private var locButtonClicked = false

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
                showMarkerOnMap()
            }
            choose_button.setOnClickListener {
                val markerLocation: LatLng? = firstMarker?.position

                if (markerLocation != null) {
                    val direction = MapFragmentDirections.actionFirstMarkerSet(
                        ParcelUserLocation(
                            markerLocation.latitude,
                            markerLocation.longitude
                        )
                    )
                    openFragment(direction)
                }
            }
        }

        if (args.setSecondMarker) {
            root_button_page.visibility = View.VISIBLE
            my_location_floating_button.visibility = View.GONE
            my_location_button.setOnClickListener {
                secondMarker?.remove()
                showMarkerOnMap()
            }
            choose_button.setOnClickListener {
                val firstMarkerLocation: LatLng? = firstMarker?.position
                val secondMarkerLocation: LatLng? = secondMarker?.position

                val firstParcelUserLoc = firstMarkerLocation?.let {
                    ParcelUserLocation(it.latitude, it.longitude)
                }
                val secondParcelUserLoc = secondMarkerLocation?.let {
                    ParcelUserLocation(it.latitude, it.longitude)
                }
                val direction = if (args.setFirstMarkerWithSecond) {
                    MapFragmentDirections.actionAllMarkersSet(secondParcelUserLoc, firstParcelUserLoc)
                } else {
                    MapFragmentDirections.actionAllMarkersSet(firstParcelUserLoc, secondParcelUserLoc)
                }
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
            val newLocation = getLocation()
            locButtonClicked = true
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
                } else {
                    Toast.makeText(requireContext(), getString(R.string.enable_location), Toast.LENGTH_LONG).show()
                    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", requireContext().packageName, null)
                        addCategory(Intent.CATEGORY_DEFAULT)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                    })
                    permissionGranted = requireContext().checkLocationPermission()

                    if (permissionGranted) {
                        restartApp()
                    } else {
                        requireActivity().finish()
                    }
                }
            }
        }
    }

    override fun onLocationChanged(p0: Location) {
        val newLocation = UserLocation(0, p0.latitude, p0.longitude)
        saveUserLocation(newLocation)
    }

    private fun restartApp(action: String? = null) {
        val i = requireContext().packageManager
            .getLaunchIntentForPackage(requireContext().packageName)?.apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                if (action != null) setAction(action)
                putExtra("open_app", true)
            }
        startActivity(i)
    }

    private suspend fun makePolyline() {
        //Получаем контекст для запросов, mapsApiKey хранит в себе String с ключом для карт
        val geoApiContext = GeoApiContext().setApiKey(getString(R.string.google_maps_key))

        //Здесь будет наш итоговый путь состоящий из набора точек
        var result: DirectionsResult? = null
        val startPoint = com.google.maps.model.LatLng(args.firstMarker?.lat ?: 0.0, args.firstMarker?.lng ?: 0.0)
        val endPoint = com.google.maps.model.LatLng(args.secondMarker?.lat ?: 0.0, args.secondMarker?.lng ?: 0.0)

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
        val path: MutableList<com.google.maps.model.LatLng>? = result?.routes?.getOrNull(0)?.overviewPolyline?.decodePath()
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
            showMarkerLocation()
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
        locationRequest.smallestDisplacement = 10f
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
                    val oldLoc = getLocation()
                    val newLoc = UserLocation(lat = latitude, lng = longitude)
                    if (locButtonClicked && oldLoc.lat != latitude && oldLoc.lng != longitude) {
                        showLocation(newLoc, permissionGranted, true)
                    } else {
                        locButtonClicked = false
                    }
                    saveUserLocation(newLoc)
                }
            }
        }
    }

    private fun showMap() {
        map_view.getMapAsync { googleMap ->
            map = googleMap.apply {
                //Show user location with delay
                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    showMarkerOnMap()
                },500)

                uiSettings.isMyLocationButtonEnabled = false
                uiSettings.isMapToolbarEnabled = false

                setOnMapLoadedCallback {
                    loading_page?.visibility = View.GONE
                    map_view?.visibility = View.VISIBLE
                }

                if (!args.setFirstMarker && !args.setSecondMarker) {
                    setOnMapLongClickListener {
                        val direction = MapFragmentDirections.actionMapLongClick(ParcelUserLocation(it.latitude, it.longitude))
                        openFragment(direction)
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

    private fun showMarkerOnMap() {
        val userLoc = getLocation()
        if (!args.makeRoot) {
            showLocation(userLoc, permissionGranted, true)
        }

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
            args.secondMarker?.let {
                firstMarker = map?.addMarker(
                    MarkerOptions().position(LatLng(it.lat, it.lng)).draggable(false)
                )
            }

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
        args.firstMarker?.let {
            firstMarker = map?.addMarker(
                MarkerOptions().
                    position(LatLng(it.lat, it.lng)).draggable(false)
            )
        }

        args.secondMarker?.let {
            secondMarker = map?.addMarker(
                MarkerOptions()
                    .position(LatLng(it.lat, it.lng))
                    .draggable(false)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
        }
    }

    private fun removeMarkers() {
        firstMarker?.remove()
        secondMarker?.remove()
    }

    private fun saveUserLocation(location: UserLocation) {
        setLocation(location)
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
                CameraUpdateFactory.newLatLngZoom(LatLng(location?.lat ?: 0.0, location?.lng ?: 0.0),
                        if (resetZoom) DEFAULT_ZOOM else map?.cameraPosition?.zoom ?: DEFAULT_ZOOM),
                ZOOM_SPEED,
                null)
    }

    private fun showMarkerLocation() {
        val builder = LatLngBounds.builder().include(firstMarker!!.position).include(secondMarker!!.position)
        val bounds = builder.build()

        val width = resources.displayMetrics.widthPixels;
        val height = resources.displayMetrics.heightPixels;
        val padding = (width * 0.10).toInt(); // offset from edges of the map 10% of screen

        map?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding))
    }
}