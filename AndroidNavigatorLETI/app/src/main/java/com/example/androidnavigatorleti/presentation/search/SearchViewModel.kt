package com.example.androidnavigatorleti.presentation.search

import androidx.lifecycle.ViewModel
import com.example.androidnavigatorleti.data.room.tables.DbSearchHistoryItem

class SearchViewModel : ViewModel() {

    private val userUc by lazy { UserUc() }

    fun addSearchHistoryItem(itemDb: DbSearchHistoryItem) {
        userUc.addSearchHistoryItem(itemDb)
    }

    fun getSearchHistory() = userUc.getSearchHistory()
}