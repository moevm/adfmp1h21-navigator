package com.example.androidnavigatorleti.ui.search

import androidx.lifecycle.ViewModel
import com.example.androidnavigatorleti.data.SearchHistoryItem
import com.example.androidnavigatorleti.uc.UserUc

class SearchViewModel : ViewModel() {

    private val userUc by lazy { UserUc() }

    fun addSearchHistoryItem(item: SearchHistoryItem) {
        userUc.addSearchHistoryItem(item)
    }

    fun getSearchHistory() = userUc.getSearchHistory()
}