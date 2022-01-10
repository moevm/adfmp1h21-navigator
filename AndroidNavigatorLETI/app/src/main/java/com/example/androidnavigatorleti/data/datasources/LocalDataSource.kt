package com.example.androidnavigatorleti.data.datasources

import com.example.androidnavigatorleti.data.room.UserDatabase
import com.example.androidnavigatorleti.data.room.tables.DbSearchHistoryItem
import com.example.androidnavigatorleti.data.room.tables.DbUserInfo
import com.example.androidnavigatorleti.data.preferences.SharedPreferencesManager

class LocalDataSource(
    private val prefsManager: SharedPreferencesManager,
    private val roomStorage: UserDatabase
) {

    suspend fun getUserOrNull(): DbUserInfo? = roomStorage.userDao().getUser()

    suspend fun insertUser(infoDb: DbUserInfo) {
        roomStorage.userDao().insertUser(infoDb)
    }

    suspend fun getSearchHistory(): List<DbSearchHistoryItem> = roomStorage.searchHistoryDao().getSearchHistory()

    suspend fun deleteSearchHistoryItem(itemDb: DbSearchHistoryItem) {
        roomStorage.searchHistoryDao().deleteSearchHistoryItem(itemDb)
    }

    suspend fun addSearchHistoryItem(itemDb: DbSearchHistoryItem) {
        roomStorage.searchHistoryDao().addSearchHistoryItem(itemDb)
    }
}