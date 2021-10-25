package com.example.androidnavigatorleti.ui.search

import com.example.androidnavigatorleti.data.domain.SearchHistoryItem
import com.example.androidnavigatorleti.ui.base.BaseFragment
import com.example.androidnavigatorleti.ui.base.BaseViewModel
import com.example.androidnavigatorleti.ui.base.EmptyViewState
import com.example.androidnavigatorleti.data.domain.UserLocation
import com.example.androidnavigatorleti.data.preferences.SharedPreferencesManager
import com.example.androidnavigatorleti.uc.UserUc
import org.koin.core.component.inject

class SearchViewModel : BaseViewModel<EmptyViewState>(EmptyViewState()) {

    private val userUc by inject<UserUc>()
    private val preferenceManager by inject<SharedPreferencesManager>()

    fun addSearchHistoryItem(itemDatabase: SearchHistoryItem) {
        userUc.addSearchHistoryItem(itemDatabase)
    }

    fun getSearchHistory() = userUc.getSearchHistory()

    fun getLocation(): UserLocation {
        val lat = preferenceManager.getDouble(SharedPreferencesManager.Keys.LAT_KEY, BaseFragment.DEFAULT_USER_LATITUDE)
        val lng = preferenceManager.getDouble(SharedPreferencesManager.Keys.LNG_KEY, BaseFragment.DEFAULT_USER_LONGITUDE)
        return UserLocation(lat = lat, lng = lng)
    }

    fun isHistoryEnabled(): Boolean = preferenceManager.getBoolean(SharedPreferencesManager.Keys.HISTORY_ENABLED, false)
}