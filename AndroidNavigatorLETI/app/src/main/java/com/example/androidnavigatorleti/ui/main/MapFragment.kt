package com.example.androidnavigatorleti.ui.main

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.androidnavigatorleti.PERMISSION_REQUEST_CODE
import com.example.androidnavigatorleti.R
import com.example.androidnavigatorleti.ui.base.BaseFragment
import com.example.androidnavigatorleti.checkLocationPermission
import com.example.androidnavigatorleti.data.domain.UserLocation
import com.example.androidnavigatorleti.requestLocationPermissions
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.GeoApiContext
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.distance_container.view.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.speed_container.view.*
import kotlinx.coroutines.Runnable
import java.util.*
import kotlin.math.floor

class MapFragment : BaseFragment<MapViewModel, MapViewState>(R.layout.fragment_map) {

    companion object {

        private const val DEFAULT_ZOOM = 12f
        private const val ZOOM_SPEED = 400
        private const val SCHEME = "package"
        private const val METERS_TOP_VALUE = 1000.0
        private const val SCREEN_PADDING = 0.1
        private const val SHOW_MARKER_DELAY = 500L
        private const val LOCATION_UPDATE_INTERVAL = 1000L
        private const val SMALLEST_DISPLACEMENT_RADIUS = 5f
        private const val HOUR_INTERVAL = 60
        private const val TIME_FROM_DISTANCE_COEFFICIENT = 0.0025
    }

    override val viewModel: MapViewModel by viewModels()
    private val args: MapFragmentArgs by navArgs()

    private var map: GoogleMap? = null
    private var firstMarker: Marker? = null
    private var secondMarker: Marker? = null
    private var permissionGranted = false

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        map_view.onCreate(savedInstanceState)

        showMap()
        initViews()
    }

    override fun onStart() {
        super.onStart()

        map_view.onStart()
    }

    override fun onResume() {
        super.onResume()

        val date = Calendar.getInstance()

        viewModel.initTrafficLightList()

        date.set(2021, 4, 27, 12, 30, 15)

        map_view.onResume()
        my_location_floating_button.setOnClickListener {
            val newLocation = viewModel.getLocation()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                permissionGranted = requireContext().checkLocationPermission()

                if (permissionGranted) {
                    startLocationUpdates()
                    map?.isMyLocationEnabled = true
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.enable_location),
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts(SCHEME, requireContext().packageName, null)
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

    private fun initViews() {
        if (permissionGranted) startLocationUpdates()

        if (args.makeRoot) makeRoot()

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
                        UserLocation(
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
                    UserLocation(it.latitude, it.longitude)
                }
                val secondParcelUserLoc = secondMarkerLocation?.let {
                    UserLocation(it.latitude, it.longitude)
                }
                val direction = if (args.setFirstMarkerWithSecond) {
                    MapFragmentDirections.actionAllMarkersSet(
                        secondParcelUserLoc,
                        firstParcelUserLoc
                    )
                } else {
                    MapFragmentDirections.actionAllMarkersSet(
                        firstParcelUserLoc,
                        secondParcelUserLoc
                    )
                }
                openFragment(direction)
            }
        }
    }

    private fun formatDistance(distance: Double): String {
        return if (distance > METERS_TOP_VALUE) {
            getString(R.string.kilometers, String.format("%.1f", distance / 1000.0))
        } else {
            getString(R.string.meters, String.format("%.0f", distance))
        }
    }

    private fun makeRoot() {
        val geoApiContext = GeoApiContext().setApiKey(getString(R.string.google_maps_key))

        speed_layout?.visibility = View.VISIBLE
        distance_layout?.visibility = View.VISIBLE
        close_floating_button.visibility = View.VISIBLE
        my_location_floating_button.visibility = View.GONE

        viewModel.buildRoute(geoApiContext, args.firstMarker, args.secondMarker)

        viewModel.polyLineLiveData.observe(viewLifecycleOwner, Observer { options ->
            //Добавляем линию на карту
            options?.let {
                polyLine = map?.addPolyline(it)
                showDoubleBlockedMarkers()
                showMarkerLocation()
            }
        })

        close_floating_button.setOnClickListener {
            viewModel.isPolylineBuild = false
            speed_layout?.visibility = View.GONE
            distance_layout?.visibility = View.GONE
            close_floating_button.visibility = View.GONE
            my_location_floating_button.visibility = View.VISIBLE
            removeMarkers()
            polyLine?.remove()
        }
    }

    private fun countDistance(
        startPoint: com.google.maps.model.LatLng,
        endPoint: com.google.maps.model.LatLng
    ): String {
        val distance = SphericalUtil.computeDistanceBetween(
            LatLng(startPoint.lat, startPoint.lng),
            LatLng(endPoint.lat, endPoint.lng)
        )

        val time = floor(distance * TIME_FROM_DISTANCE_COEFFICIENT).toInt()

        return if (time >= HOUR_INTERVAL) {
            val hours = time / HOUR_INTERVAL
            val minutes = time % HOUR_INTERVAL
            getString(R.string.route_time_text_hours, hours.toString(), minutes.toString())
        } else {
            getString(R.string.route_time_text_min, time.toString())
        }
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = LOCATION_UPDATE_INTERVAL
        locationRequest.fastestInterval = LOCATION_UPDATE_INTERVAL
        locationRequest.smallestDisplacement = SMALLEST_DISPLACEMENT_RADIUS
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

                    val oldLoc = viewModel.getLocation()
                    val newLoc = UserLocation(lat = latitude, lng = longitude)

                    viewModel.newLocation = newLoc

                    if (locButtonClicked && oldLoc.lat != latitude && oldLoc.lng != longitude) {
                        showLocation(newLoc, permissionGranted, true)
                    } else {
                        locButtonClicked = false
                    }
                    viewModel.saveUserLocation(newLoc)
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
                }, SHOW_MARKER_DELAY)

                uiSettings.isMyLocationButtonEnabled = false
                uiSettings.isMapToolbarEnabled = false

                setOnMapLoadedCallback {
                    loading_page?.visibility = View.GONE
                    map_view?.visibility = View.VISIBLE
                }

                if (!args.setFirstMarker && !args.setSecondMarker) {
                    setOnMapLongClickListener {
                        val direction = MapFragmentDirections.actionMapLongClick(
                            UserLocation(
                                it.latitude,
                                it.longitude
                            )
                        )
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
        val userLoc = viewModel.getLocation()
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
                MarkerOptions().position(LatLng(it.lat, it.lng)).draggable(false)
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
            CameraUpdateFactory.newLatLngZoom(
                LatLng(location?.lat ?: 0.0, location?.lng ?: 0.0),
                if (resetZoom) DEFAULT_ZOOM else map?.cameraPosition?.zoom ?: DEFAULT_ZOOM
            ),
            ZOOM_SPEED,
            null
        )
    }

    private fun showMarkerLocation() {
        val builder =
            LatLngBounds.builder().include(firstMarker!!.position).include(secondMarker!!.position)
        val bounds = builder.build()

        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        // offset from edges of the map 10% of screen
        val padding = (width * SCREEN_PADDING).toInt()

        map?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding))
    }

    override fun renderState(state: MapViewState) {
        speed_layout?.current_speed?.text = state.currentSpeed.toString()
        speed_layout?.min_speed?.text = state.minSpeed.toString()
        speed_layout?.max_speed?.text = state.maxSpeed.toString()
        distance_layout?.traffic_distance?.text = formatDistance(state.trafficLightDistance)
        distance_layout?.point_distance?.text = formatDistance(state.currentDistance)
    }
}