package com.example.androidnavigatorleti.ui.profile

import androidx.lifecycle.ViewModel
import com.example.androidnavigatorleti.data.UserInfo
import com.example.androidnavigatorleti.uc.UserUc

class RegistrationVIewModel : ViewModel() {

    private val userUc by lazy { UserUc() }

    fun insertUser(info: UserInfo) {
        userUc.insertUser(info)
    }
}