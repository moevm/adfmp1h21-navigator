package com.example.androidnavigatorleti.presentation.menu

import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.androidnavigatorleti.R
import com.example.androidnavigatorleti.common.base.BaseFragment
import com.example.androidnavigatorleti.common.base.EmptyViewModel
import com.example.androidnavigatorleti.common.base.EmptyViewState
import kotlinx.android.synthetic.main.fragment_menu.*

class MenuFragment : BaseFragment<EmptyViewModel, EmptyViewState>(R.layout.fragment_menu) {

    override val viewModel: EmptyViewModel by viewModels()

    override fun renderState(state: EmptyViewState) {
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
}