package com.example.androidnavigatorleti.ui.search

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
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

    private val viewModel: SearchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val geocoder = Geocoder(requireActivity())

        if (args.queryName.isNotEmpty()) {
            after_search_layout.setQuery(args.queryName, true)
        }

        args.point?.let {
            val userLoc = getLocation()
            val userLocName = geocoder.getFromLocation(userLoc.lat, userLoc.lng, 1).getOrNull(0)
                ?.getAddressLine(0)

            before_search_layout.setQuery(
                userLocName,
                true
            )
            val loc = geocoder.getFromLocation(it.lat, it.lng, 1)
            after_search_layout.setQuery(loc[0].getAddressLine(0).toString(), true)
        }

        args.firstMarker?.let {
            val loc = geocoder.getFromLocation(it.lat, it.lng, 1)
            before_search_layout.setQuery(
                loc.getOrNull(0)?.getAddressLine(0).toString().substringBefore(getString(R.string.city_delimiter)),
                true
            )
        }

        args.secondMarker?.let {
            val loc = geocoder.getFromLocation(it.lat, it.lng, 1)
            after_search_layout.setQuery(
                loc.getOrNull(0)?.getAddressLine(0).toString().substringBefore(getString(R.string.city_delimiter)),
                true
            )
        }

        find_point_button.setOnClickListener {
            if (before_search_layout.query.isEmpty()) {
                if (after_search_layout.query.isEmpty()) {
                    openFragment(SearchFragmentDirections.actionSetFirstMarker(true))
                } else {
                    geocoder.getFromLocationName(after_search_layout.query.toString(), 1).getOrNull(
                        0
                    )?.let {
                        openFragment(
                            SearchFragmentDirections.actionSetFirstMarkerWithSecondMarker(
                                setFirstMarkerWithSecond = true,
                                setSecondMarker = true,
                                secondMarker = ParcelUserLocation(it.latitude, it.longitude)
                            )
                        )
                    } ?: Toast.makeText(
                        requireContext(),
                        getString(R.string.enter_another_address),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                geocoder.getFromLocationName(
                    before_search_layout.query.toString(),
                    1
                ).getOrNull(0)?.let {
                    openFragment(
                        SearchFragmentDirections.actionSetSecondMarker(
                            true,
                            ParcelUserLocation(it.latitude, it.longitude)
                        )
                    )
                } ?: Toast.makeText(
                    requireContext(),
                    getString(R.string.enter_another_address),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        make_root_button.setOnClickListener {
            if (prefsManager.getBoolean(HISTORY_ENABLED, false)) {
                val list = viewModel.getSearchHistory()
                var writeNewVal = true
                list.forEach {
                    if (it.place == after_search_layout.query) {
                        writeNewVal = false
                    }
                }
                if (writeNewVal) {
                    viewModel.addSearchHistoryItem(SearchHistoryItem(place = after_search_layout.query.toString()))
                }
            }
            if (before_search_layout.query.isNullOrEmpty() || after_search_layout.query.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.enter_address),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val firstLoc =
                    geocoder.getFromLocationName(before_search_layout.query.toString(), 1)
                        .getOrNull(0)
                val secondLoc =
                    geocoder.getFromLocationName(after_search_layout.query.toString(), 1)
                        .getOrNull(0)

                if (firstLoc != null && secondLoc != null) {
                    val direction = SearchFragmentDirections.actionMakeRoot(
                        true,
                        ParcelUserLocation(firstLoc.latitude, firstLoc.longitude),
                        ParcelUserLocation(secondLoc.latitude, secondLoc.longitude)
                    )
                    openFragment(direction)
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.enter_another_address),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}