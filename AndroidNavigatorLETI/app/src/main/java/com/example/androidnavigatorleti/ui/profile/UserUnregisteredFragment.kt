package com.example.androidnavigatorleti.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.androidnavigatorleti.R
import com.example.androidnavigatorleti.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_unregistered.*

class UserUnregisteredFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_unregistered, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        register_button.setOnClickListener {
            openFragment(R.id.registration)
        }
    }
}