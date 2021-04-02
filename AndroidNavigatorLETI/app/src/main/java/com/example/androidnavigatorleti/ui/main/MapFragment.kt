package com.example.androidnavigatorleti.ui.main

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.androidnavigatorleti.*
import com.example.androidnavigatorleti.base.BaseFragment
import com.example.androidnavigatorleti.data.UserLocation
import com.example.androidnavigatorleti.preferences.SharedPreferencesManager.Keys.LAT_KEY
import com.example.androidnavigatorleti.preferences.SharedPreferencesManager.Keys.LNG_KEY
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MapFragment : BaseFragment(), CoroutineScope, LocationListener {

    companion object {

        private const val DEFAULT_ZOOM = 12f
        private const val DELTA_ZOOM = 1.5f
        private const val ZOOM_SPEED = 400
        private const val DEFAULT_USER_LATITUDE = 30.315492
        private const val DEFAULT_USER_LONGITUDE = 59.939007
        const val GOOGLE_PACKAGE = "com.google.android.apps.maps"
        const val GOOGLE_ERROR_TOAST = "Google map app isn't available"
        const val SHARING_INTENT_TYPE = "text/plain"
    }

    private var map: GoogleMap? = null

    private var permissionGranted = false

    private var initMapJob: Job? = null
    private var loadPlacesJob: Job? = null
    private var locationJob: Job? = null
    private var placeRulesJob: Job? = null

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    val args: MapFragmentArgs by navArgs()

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionGranted = requireContext().checkLocationAndForegroundServicePermissions()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_map, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        map_view.onCreate(savedInstanceState)
        showMap()

        if (args.makeRoot) {
            close_floating_button.visibility = View.VISIBLE
            search_container.visibility = View.VISIBLE
            my_location_floating_button.visibility = View.GONE
            close_floating_button.setOnClickListener {
                close_floating_button.visibility = View.GONE
                search_container.visibility = View.GONE
                my_location_floating_button.visibility = View.VISIBLE
            }
        }

        if (args.setMarker) {
            root_button_page.visibility = View.VISIBLE
            my_location_floating_button.visibility = View.GONE
            choose_button.setOnClickListener {
                openFragment(R.id.search)
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
        //my_location_floating_button.setOnClickListener { getDeviceLocation(permissionGranted) }
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
                permissionGranted = checkLocationAndForegroundServicePermissions()

                if (permissionGranted) {
                    map?.isMyLocationEnabled = true
                }

                //getDeviceLocation(permissionGranted)
            }
        }
    }

    override fun onLocationChanged(p0: Location) {
        val newLocation = UserLocation(p0.latitude, p0.longitude)
        saveUserLocation(newLocation)
    }

    fun showMap() {
        map_view.getMapAsync { googleMap ->
            map = googleMap.apply {
                uiSettings.isMyLocationButtonEnabled = false
                uiSettings.isMapToolbarEnabled = false

                setOnMapLoadedCallback {
                    loading_page.visibility = View.GONE
                    map_view.visibility = View.VISIBLE
                }

                setOnMapLongClickListener {
                    openFragment(R.id.search)
                }
            }

            if (permissionGranted) {
                map?.isMyLocationEnabled = true
                //getDeviceLocation(permissionGranted)
            } else {
                requestLocationAndForegroundServicePermissions()
            }
        }
    }

    private fun getDeviceLocation(permissionIsGranted: Boolean) {
        if (checkJob(locationJob)) {
            locationJob = launch(this.coroutineContext) {
                withContext(Dispatchers.IO) {
                    val location = getCurrentUserLocation()
                    if (location == null && permissionIsGranted) {
                        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
                        mFusedLocationClient.lastLocation.addOnCompleteListener { task ->
                            val lastLocation = task.result
                            if (lastLocation != null) {
                                showLocation(UserLocation(lastLocation.latitude, lastLocation.longitude), permissionIsGranted)
                            } else {
                                showLocation(getDefaultUserLocation(), permissionIsGranted)
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            showLocation(location, permissionIsGranted)
                        }
                    }
                }
            }
        }
    }

    private fun getCurrentUserLocation(): UserLocation? {
        getCurrentLocation()
        return getUserLocation()
    }

    private fun getCurrentLocation() = try {
        getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, this)
        getLocationManager().requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, this)
    } catch (e: SecurityException) {
        getUserLocation()
    }

    private fun getUserLocation(): UserLocation? =
            UserLocation(
                    prefsManager.getDouble(LAT_KEY, -1.0),
                    prefsManager.getDouble(LNG_KEY, -1.0)
            )

    private fun saveUserLocation(location: UserLocation) {
        prefsManager.putDouble(LAT_KEY, location.lat)
        prefsManager.putDouble(LNG_KEY, location.lng)
    }

    private fun showLocation(
            location: UserLocation?,
            permissionIsGranted: Boolean,
            resetZoom: Boolean = true
    ) {
        if (location != null && permissionIsGranted
                && getLocationManager().isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            showDeviceLocation(location, resetZoom)
        } else {
            getDefaultDeviceLocation()
        }
    }

    private fun getDefaultDeviceLocation() {
        val defaultLatLng = UserLocation(DEFAULT_USER_LATITUDE, DEFAULT_USER_LONGITUDE)
        showDeviceLocation(UserLocation(defaultLatLng.lat, defaultLatLng.lng), true)
    }

    private fun getDefaultUserLocation(): UserLocation? =
            UserLocation(DEFAULT_USER_LATITUDE, DEFAULT_USER_LONGITUDE)

    private fun showDeviceLocation(location: UserLocation?, resetZoom: Boolean) {
        map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(LatLng(location!!.lat, location.lng),
                        if (resetZoom) DEFAULT_ZOOM else map?.cameraPosition?.zoom ?: DEFAULT_ZOOM),
                ZOOM_SPEED,
                null)
    }
}