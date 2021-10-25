package com.example.androidnavigatorleti.ui.search

import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.androidnavigatorleti.R
import com.example.androidnavigatorleti.data.domain.SearchHistoryItem
import com.example.androidnavigatorleti.ui.base.BaseFragment
import com.example.androidnavigatorleti.ui.base.EmptyViewState
import com.example.androidnavigatorleti.data.domain.UserLocation
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : BaseFragment<SearchViewModel, EmptyViewState>(R.layout.fragment_search) {

    override val viewModel: SearchViewModel by viewModels()

    private val args: SearchFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        val geocoder = Geocoder(requireActivity())

        if (args.queryName.isNotEmpty()) {
            after_search_layout.setQuery(args.queryName, true)
        }

        args.point?.let {
            val userLoc = viewModel.getLocation()
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
                                secondMarker = UserLocation(it.latitude, it.longitude)
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
                            UserLocation(it.latitude, it.longitude)
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
            if (viewModel.isHistoryEnabled()) {
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
                        UserLocation(firstLoc.latitude, firstLoc.longitude),
                        UserLocation(secondLoc.latitude, secondLoc.longitude)
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

    override fun renderState(state: EmptyViewState) {
    }
}