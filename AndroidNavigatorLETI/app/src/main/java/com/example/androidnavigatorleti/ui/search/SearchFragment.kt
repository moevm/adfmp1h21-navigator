package com.example.androidnavigatorleti.ui.search

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.example.androidnavigatorleti.NavigatorApp
import com.example.androidnavigatorleti.R
import com.example.androidnavigatorleti.base.BaseFragment
import com.example.androidnavigatorleti.data.ParcelUserLocation
import com.example.androidnavigatorleti.data.SearchHistoryItem
import com.example.androidnavigatorleti.preferences.SharedPreferencesManager.Keys.HISTORY_ENABLED
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : BaseFragment() {

    private val args: SearchFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val geocoder = Geocoder(requireActivity())

        if (args.point != null) {
            val userLoc = NavigatorApp.userDao.getLocation()
            val userLocName = geocoder.getFromLocation(userLoc.lat, userLoc.lng, 1)[0].getAddressLine(0)

            before_search_layout.setQuery(
                userLocName,
                true
            )
            val loc = geocoder.getFromLocation(args.point!!.lat, args.point!!.lng, 1)
            after_search_layout.setQuery(loc[0].getAddressLine(0).toString(), true)
        }

        if (args.firstMarker != null) {
            val loc = geocoder.getFromLocation(args.firstMarker!!.lat, args.firstMarker!!.lng, 1)
            before_search_layout.setQuery(
                loc[0].getAddressLine(0).toString().substringBefore(getString(R.string.city_delimiter)),
                true
            )
        }

        if (args.secondMarker != null) {
            val loc = geocoder.getFromLocation(args.secondMarker!!.lat, args.secondMarker!!.lng, 1)
            after_search_layout.setQuery(
                loc[0].getAddressLine(0).toString().substringBefore(getString(R.string.city_delimiter)),
                true
            )
        }

        find_point_button.setOnClickListener {
            if (before_search_layout.query.isEmpty()) {
                openFragment(SearchFragmentDirections.actionSetFirstMarker(true))
            } else {
                geocoder.getFromLocationName(before_search_layout.query.toString(), 1).getOrNull(0)?.let {
                    openFragment(SearchFragmentDirections.actionSetSecondMarker(
                        true,
                        ParcelUserLocation(it.latitude, it.longitude)
                    ))
                } ?: Toast.makeText(requireContext(), getString(R.string.enter_another_address), Toast.LENGTH_SHORT).show()
            }
        }

        make_root_button.setOnClickListener {
            if (prefsManager.getBoolean(HISTORY_ENABLED, false)) {
                NavigatorApp.userDao.addSearchHistoryItem(SearchHistoryItem(place = after_search_layout.query.toString()))
            }
            val firstLoc = geocoder.getFromLocationName(before_search_layout.query.toString(), 1).getOrNull(0)
            val secondLoc = geocoder.getFromLocationName(after_search_layout.query.toString(), 1).getOrNull(0)

            Log.d("HIHI", firstLoc.toString())
            Log.d("HIHI", secondLoc.toString())

            val direction = SearchFragmentDirections.actionMakeRoot(
                true,
                ParcelUserLocation(firstLoc!!.latitude, firstLoc.longitude),
                ParcelUserLocation(secondLoc!!.latitude, secondLoc.longitude)
            )
            openFragment(direction)
        }
    }
}