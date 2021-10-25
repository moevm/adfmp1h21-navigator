package com.example.androidnavigatorleti.data.repositories

import android.content.Context
import com.example.androidnavigatorleti.data.datasources.LocalDataSource
import com.example.androidnavigatorleti.data.datasources.RemoteDataSource
import com.example.androidnavigatorleti.data.room.tables.SearchHistoryItem
import com.example.androidnavigatorleti.data.room.tables.UserInfo

class UserRepositoryImpl(
    private val context: Context,
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : UserRepository {

    override fun getUserOrNull(): UserInfo? = localDataSource.getUserOrNull()

    override fun getSearchHistory(): List<SearchHistoryItem> = localDataSource.getSearchHistory()

    override fun insertUser(info: UserInfo) {
        localDataSource.insertUser(info)
    }

    override fun deleteHistoryItem(item: SearchHistoryItem) {
        localDataSource.deleteHistoryItem(item)
    }

    override fun addSearchHistoryItem(item: SearchHistoryItem) {
        localDataSource.addSearchHistoryItem(item)
    }
}