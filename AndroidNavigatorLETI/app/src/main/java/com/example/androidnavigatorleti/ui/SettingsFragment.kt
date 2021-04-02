package com.example.androidnavigatorleti.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.androidnavigatorleti.R
import com.example.androidnavigatorleti.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.settings_switch.view.*

class SettingsFragment: BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        geo_layout?.switch_text?.text = getString(R.string.geolocation)
        history_layout?.switch_text?.text = getString(R.string.save_search_history)
    }
}