package com.example.androidnavigatorleti.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.androidnavigatorleti.R
import com.example.androidnavigatorleti.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : BaseFragment() {

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

        find_point_button.setOnClickListener {
            val direction = SearchFragmentDirections.actionSetMarker(true)
            openFragment(direction)
        }

        make_root_button.setOnClickListener {
            val direction = SearchFragmentDirections.actionMakeRoot(true)
            openFragment(direction)
        }
    }
}