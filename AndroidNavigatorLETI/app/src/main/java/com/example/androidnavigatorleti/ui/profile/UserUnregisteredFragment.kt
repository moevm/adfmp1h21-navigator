package com.example.androidnavigatorleti.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.androidnavigatorleti.R
import com.example.androidnavigatorleti.ui.base.BaseFragment
import com.example.androidnavigatorleti.ui.base.EmptyViewModel
import com.example.androidnavigatorleti.ui.base.EmptyViewState
import kotlinx.android.synthetic.main.fragment_unregistered.*

class UserUnregisteredFragment : BaseFragment<EmptyViewModel, EmptyViewState>(R.layout.fragment_unregistered) {

    override val viewModel: EmptyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        register_button.setOnClickListener {
            openFragment(R.id.registration)
        }
    }

    override fun renderState(state: EmptyViewState) {
    }
}