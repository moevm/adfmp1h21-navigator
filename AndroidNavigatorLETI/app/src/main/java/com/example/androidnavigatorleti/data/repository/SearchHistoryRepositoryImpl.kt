package com.example.androidnavigatorleti.data.repository

import com.example.androidnavigatorleti.data.datasources.LocalDataSource
import com.example.androidnavigatorleti.data.datasources.RemoteDataSource
import com.example.androidnavigatorleti.data.room.tables.DbSearchHistoryItem
import com.example.androidnavigatorleti.domain.repository.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
): SearchHistoryRepository {

    override suspend fun getSearchHistory(): List<DbSearchHistoryItem> =
        localDataSource.getSearchHistory()

    override suspend fun deleteSearchHistoryItem(itemDb: DbSearchHistoryItem) {
        localDataSource.deleteSearchHistoryItem(itemDb)
    }

    override suspend fun addSearchHistoryItem(itemDb: DbSearchHistoryItem) {
        localDataSource.addSearchHistoryItem(itemDb)
    }
}