package com.example.androidnavigatorleti.base

import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.androidnavigatorleti.preferences.SharedPreferencesManager
import kotlinx.coroutines.Job

open class BaseFragment : Fragment() {

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

    fun checkJob(job: Job?) = job == null || job.isCompleted

    fun getLocationManager() = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

}