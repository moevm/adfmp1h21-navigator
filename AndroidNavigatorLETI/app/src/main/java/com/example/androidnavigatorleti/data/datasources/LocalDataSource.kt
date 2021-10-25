package com.example.androidnavigatorleti.data.datasources

import android.content.Context
import com.example.androidnavigatorleti.data.room.UserDatabase
import com.example.androidnavigatorleti.data.preferences.SharedPreferencesManager
import com.example.androidnavigatorleti.data.room.tables.SearchHistoryItem
import com.example.androidnavigatorleti.data.room.tables.UserInfo

class LocalDataSource(
    private val context: Context,
    private val prefsManager: SharedPreferencesManager,
    private val roomStorage: UserDatabase
) {

    fun getUserOrNull(): UserInfo? = roomStorage.userDao().getUser()

    fun getSearchHistory(): List<SearchHistoryItem> = roomStorage.searchHistoryDao().getSearchHistory()

    fun insertUser(info: UserInfo) {
        roomStorage.userDao().insertUser(info)
    }

    fun deleteHistoryItem(item: SearchHistoryItem) {
        roomStorage.searchHistoryDao().deleteSearchHistoryItem(item)
    }

    fun addSearchHistoryItem(item: SearchHistoryItem) {
        roomStorage.searchHistoryDao().addSearchHistoryItem(item)
    }
}