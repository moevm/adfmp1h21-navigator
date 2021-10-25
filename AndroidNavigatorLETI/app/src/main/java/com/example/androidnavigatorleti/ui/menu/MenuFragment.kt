package com.example.androidnavigatorleti.ui.menu

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.androidnavigatorleti.R
import com.example.androidnavigatorleti.ui.base.BaseFragment
import com.example.androidnavigatorleti.ui.base.EmptyViewModel
import com.example.androidnavigatorleti.ui.base.EmptyViewState
import kotlinx.android.synthetic.main.fragment_menu.*

class MenuFragment : BaseFragment<EmptyViewModel, EmptyViewState>(R.layout.fragment_menu) {

    override val viewModel: EmptyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        menu_navigation_view?.let {
            it.menu.add(getString(R.string.settings)).apply {
                isCheckable = false
                icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_settings)
                setOnMenuItemClickListener {
                    openFragment(R.id.settings)
                    true
                }
            }
            it.menu.add(getString(R.string.about)).apply {
                isCheckable = false
                icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_about)
                setOnMenuItemClickListener {
                    openFragment(R.id.about)
                    true
                }
            }
        }

        menu_navigation_view?.setupWithNavController(findNavController())
    }

    override fun renderState(state: EmptyViewState) {
    }
}