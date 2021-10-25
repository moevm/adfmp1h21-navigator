package com.example.androidnavigatorleti.ui.history

import com.example.androidnavigatorleti.data.domain.SearchHistoryItem
import com.example.androidnavigatorleti.data.domain.UserInfo
import com.example.androidnavigatorleti.ui.base.BaseViewModel
import com.example.androidnavigatorleti.ui.base.EmptyViewState
import com.example.androidnavigatorleti.uc.UserUc
import org.koin.core.component.inject

class HistoryViewModel : BaseViewModel<EmptyViewState>(EmptyViewState()) {

    private val userUc by inject<UserUc>()

    fun getUser(): UserInfo? = userUc.getUserOrNull()

    fun getSearchHistory() = userUc.getSearchHistory()

    fun deleteItem(itemDatabase: SearchHistoryItem) {
        userUc.deleteUser(itemDatabase)
    }
}