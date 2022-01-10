package com.example.androidnavigatorleti.presentation.profile

import com.example.androidnavigatorleti.common.base.BaseViewModel
import com.example.androidnavigatorleti.common.base.EmptyViewState
import com.example.androidnavigatorleti.data.room.tables.DbUserInfo

class RegistrationViewModel : BaseViewModel<EmptyViewState>(EmptyViewState()) {

    private val userUc by lazy { UserUc() }

    fun insertUser(infoDb: DbUserInfo) {
        userUc.insertUser(infoDb)
    }
}