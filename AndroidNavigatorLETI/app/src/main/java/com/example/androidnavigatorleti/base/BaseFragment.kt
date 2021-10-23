package com.example.androidnavigatorleti.base

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.androidnavigatorleti.data.UserLocation
import com.example.androidnavigatorleti.preferences.SharedPreferencesManager
import com.instacart.library.truetime.TrueTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class BaseFragment : Fragment() {

    companion object {

        const val DEFAULT_USER_LATITUDE = 30.315492
        const val DEFAULT_USER_LONGITUDE = 59.939007
    }

    protected lateinit var prefsManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefsManager = SharedPreferencesManager(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    if (!TrueTime.isInitialized()) {
                        TrueTime.build()
                            .withNtpHost("time.apple.com")
                            .withRootDelayMax(750F)
                            .withRootDispersionMax(100F)
                            .withServerResponseDelayMax(750)
                            .initialize()
                    } else {
                        Log.d("initTimeException", "already initialized")
                    }
                } catch (e: Exception) {
                    Log.e("initTimeException", "Init true time exception: $e")
                }
            }
        }
    }

    protected fun openFragment(resId: Int) {
        with(findNavController()) {
            try {
                navigate(resId)
            } catch (e: IllegalArgumentException) {
            }
        }
    }

    protected fun openFragment(direction: NavDirections) {
        with(findNavController()) {
            try {
                navigate(direction)
            } catch (e: IllegalArgumentException) {
            }
        }
    }


    fun setLocation(location: UserLocation) {
        prefsManager.putDouble(SharedPreferencesManager.Keys.LAT_KEY, location.lat)
        prefsManager.putDouble(SharedPreferencesManager.Keys.LNG_KEY, location.lng)
    }

    fun getLocation(): UserLocation {
        val lat = prefsManager.getDouble(SharedPreferencesManager.Keys.LAT_KEY, DEFAULT_USER_LATITUDE)
        val lng = prefsManager.getDouble(SharedPreferencesManager.Keys.LNG_KEY, DEFAULT_USER_LONGITUDE)
        return UserLocation(lat = lat, lng = lng)
    }
}