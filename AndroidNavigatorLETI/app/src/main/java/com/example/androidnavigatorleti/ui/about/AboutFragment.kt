package com.example.androidnavigatorleti.ui.about

import com.example.androidnavigatorleti.R
import androidx.fragment.app.viewModels
import com.example.androidnavigatorleti.ui.base.BaseFragment
import com.example.androidnavigatorleti.ui.base.EmptyViewModel
import com.example.androidnavigatorleti.ui.base.EmptyViewState

class AboutFragment : BaseFragment<EmptyViewModel, EmptyViewState>(R.layout.fragment_about) {

    override val viewModel: EmptyViewModel by viewModels()

    override fun renderState(state: EmptyViewState) {
    }
}