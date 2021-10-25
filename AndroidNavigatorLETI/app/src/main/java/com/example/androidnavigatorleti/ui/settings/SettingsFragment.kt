package com.example.androidnavigatorleti.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.viewModels
import com.example.androidnavigatorleti.PERMISSION_REQUEST_CODE
import com.example.androidnavigatorleti.R
import com.example.androidnavigatorleti.ui.base.BaseFragment
import com.example.androidnavigatorleti.ui.base.EmptyViewState
import com.example.androidnavigatorleti.checkLocationPermission
import com.example.androidnavigatorleti.requestLocationPermissions
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.settings_switch.view.*

class SettingsFragment: BaseFragment<SettingsViewModel, EmptyViewState>(R.layout.fragment_settings) {

    override val viewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        geo_layout?.switch_text?.text = getString(R.string.geolocation)
        history_layout?.switch_text?.text = getString(R.string.save_search_history)

        with(geo_layout?.nav_switch!!) {
            isChecked = requireContext().checkLocationPermission()

            setOnCheckedChangeListener { _, _ ->
                if (isChecked) {
                    requestLocationPermissions()
                } else {
                    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                        addCategory(Intent.CATEGORY_DEFAULT)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                    })
                    isChecked = requireContext().checkLocationPermission()
                }
            }
        }

        with(history_layout?.nav_switch!!) {
            isChecked = viewModel.isHistoryEnabled()

            setOnCheckedChangeListener { _, _ ->
                viewModel.setHistoryEnabled(isChecked)
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

    override fun renderState(state: EmptyViewState) {
    }
}