package com.example.androidnavigatorleti.data.repositories

import android.content.Context
import com.example.androidnavigatorleti.data.datasources.LocalDataSource
import com.example.androidnavigatorleti.data.datasources.RemoteDataSource
import com.example.androidnavigatorleti.data.domain.SearchHistoryItem
import com.example.androidnavigatorleti.data.domain.UserInfo
import com.example.androidnavigatorleti.data.room.tables.DatabaseSearchHistoryItem
import com.example.androidnavigatorleti.data.room.tables.DatabaseUserInfo

class UserRepositoryImpl(
    private val context: Context,
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : UserRepository {

    override fun getUserOrNull(): UserInfo? = localDataSource.getUserOrNull()?.mapToDomain()

    override fun getSearchHistory(): List<SearchHistoryItem> = localDataSource.getSearchHistory().map { it.mapToDomain() }

    override fun insertUser(info: UserInfo) {
        localDataSource.insertUser(DatabaseUserInfo(info))
    }

    override fun deleteHistoryItem(itemDatabase: SearchHistoryItem) {
        localDataSource.deleteHistoryItem(DatabaseSearchHistoryItem(itemDatabase))
    }

    override fun addSearchHistoryItem(itemDatabase: SearchHistoryItem) {
        localDataSource.addSearchHistoryItem(DatabaseSearchHistoryItem(itemDatabase))
    }
}