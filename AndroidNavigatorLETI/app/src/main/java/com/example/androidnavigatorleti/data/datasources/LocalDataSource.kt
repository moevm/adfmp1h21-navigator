package com.example.androidnavigatorleti.data.datasources

import android.content.Context
import com.example.androidnavigatorleti.data.room.UserDatabase
import com.example.androidnavigatorleti.data.preferences.SharedPreferencesManager
import com.example.androidnavigatorleti.data.room.tables.DatabaseSearchHistoryItem
import com.example.androidnavigatorleti.data.room.tables.DatabaseUserInfo

class LocalDataSource(
    private val context: Context,
    private val prefsManager: SharedPreferencesManager,
    private val roomStorage: UserDatabase
) {

    fun getUserOrNull(): DatabaseUserInfo? = roomStorage.userDao().getUser()

    fun getSearchHistory(): List<DatabaseSearchHistoryItem> = roomStorage.searchHistoryDao().getSearchHistory()

    fun insertUser(info: DatabaseUserInfo) {
        roomStorage.userDao().insertUser(info)
    }

    fun deleteHistoryItem(itemDatabase: DatabaseSearchHistoryItem) {
        roomStorage.searchHistoryDao().deleteSearchHistoryItem(itemDatabase)
    }

    fun addSearchHistoryItem(itemDatabase: DatabaseSearchHistoryItem) {
        roomStorage.searchHistoryDao().addSearchHistoryItem(itemDatabase)
    }
}