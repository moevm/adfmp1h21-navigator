package com.example.androidnavigatorleti.ui.profile

import com.example.androidnavigatorleti.data.domain.UserInfo
import com.example.androidnavigatorleti.ui.base.BaseViewModel
import com.example.androidnavigatorleti.ui.base.EmptyViewState
import com.example.androidnavigatorleti.uc.UserUc
import org.koin.core.component.inject

class RegistrationViewModel : BaseViewModel<EmptyViewState>(EmptyViewState()) {

    private val userUc by inject<UserUc>()

    fun insertUser(info: UserInfo) {
        userUc.insertUser(info)
    }
}