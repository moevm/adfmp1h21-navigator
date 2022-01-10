package com.example.androidnavigatorleti.domain.repository

import com.example.androidnavigatorleti.data.room.tables.DbSearchHistoryItem

interface SearchHistoryRepository {

    suspend fun getSearchHistory(): List<DbSearchHistoryItem>

    suspend fun deleteSearchHistoryItem(itemDb: DbSearchHistoryItem)

    suspend fun addSearchHistoryItem(itemDb: DbSearchHistoryItem)
}