package com.example.androidnavigatorleti.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.androidnavigatorleti.R
import com.example.androidnavigatorleti.base.BaseFragment
import com.example.androidnavigatorleti.preferences.SharedPreferencesManager.Keys.NAME

class HistoryFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_history, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (prefsManager.getString(NAME, "").isEmpty()) {
            openFragment(R.id.unregistered)
        } else {

        }
    }
}