package com.example.androidnavigatorleti.presentation.about

import androidx.fragment.app.viewModels
import com.example.androidnavigatorleti.R
import com.example.androidnavigatorleti.common.base.BaseFragment
import com.example.androidnavigatorleti.common.base.EmptyViewModel
import com.example.androidnavigatorleti.common.base.EmptyViewState

class AboutFragment : BaseFragment<EmptyViewModel, EmptyViewState>(R.layout.fragment_about) {

    override val viewModel: EmptyViewModel by viewModels()

    override fun renderState(state: EmptyViewState) {
    }
}