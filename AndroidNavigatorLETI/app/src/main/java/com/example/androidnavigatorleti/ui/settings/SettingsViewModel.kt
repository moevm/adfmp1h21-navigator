package com.example.androidnavigatorleti.ui.settings

import com.example.androidnavigatorleti.ui.base.BaseViewModel
import com.example.androidnavigatorleti.ui.base.EmptyViewState
import com.example.androidnavigatorleti.data.preferences.SharedPreferencesManager
import org.koin.core.component.inject

class SettingsViewModel: BaseViewModel<EmptyViewState>(EmptyViewState()) {

    private val preferenceManager by inject<SharedPreferencesManager>()

    fun isHistoryEnabled(): Boolean = preferenceManager.getBoolean(SharedPreferencesManager.Keys.HISTORY_ENABLED, false)

    fun setHistoryEnabled(isEnabled: Boolean) {
        preferenceManager.putBoolean(SharedPreferencesManager.Keys.HISTORY_ENABLED, isEnabled)
    }
}