package com.example.androidnavigatorleti.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.androidnavigatorleti.PERMISSION_REQUEST_CODE
import com.example.androidnavigatorleti.R
import com.example.androidnavigatorleti.base.BaseFragment
import com.example.androidnavigatorleti.checkLocationPermission
import com.example.androidnavigatorleti.preferences.SharedPreferencesManager
import com.example.androidnavigatorleti.requestLocationPermissions
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.settings_switch.view.*

class SettingsFragment: BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        geo_layout?.switch_text?.text = getString(R.string.geolocation)
        history_layout?.switch_text?.text = getString(R.string.save_search_history)

        with(geo_layout?.nav_switch!!) {
            isChecked = requireContext().checkLocationPermission()

            setOnCheckedChangeListener { _, _ ->
                if (isChecked) requestLocationPermissions()
            }
        }

        with(history_layout?.nav_switch!!) {
            isChecked = prefsManager.getBoolean(SharedPreferencesManager.Keys.HISTORY_ENABLED, false)

            setOnCheckedChangeListener { _, _ ->
                prefsManager.putBoolean(SharedPreferencesManager.Keys.HISTORY_ENABLED, isChecked)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                geo_layout?.nav_switch?.isChecked = requireContext().checkLocationPermission()
            }
        }
    }
}