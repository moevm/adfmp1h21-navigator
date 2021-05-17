package com.example.androidnavigatorleti.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.androidnavigatorleti.data.UserLocation
import com.example.androidnavigatorleti.preferences.SharedPreferencesManager
import kotlinx.coroutines.Job

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