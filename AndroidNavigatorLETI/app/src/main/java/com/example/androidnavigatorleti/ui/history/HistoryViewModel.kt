package com.example.androidnavigatorleti.ui.history

import androidx.lifecycle.ViewModel
import com.example.androidnavigatorleti.data.SearchHistoryItem
import com.example.androidnavigatorleti.data.UserInfo
import com.example.androidnavigatorleti.uc.UserUc

class HistoryViewModel : ViewModel() {

    private val userUc by lazy { UserUc() }

    fun getUser(): UserInfo? = userUc.getUserOrNull()

    fun getSearchHistory() = userUc.getSearchHistory()

    fun deleteItem(item: SearchHistoryItem) {
        userUc.deleteItem(item)
    }
}