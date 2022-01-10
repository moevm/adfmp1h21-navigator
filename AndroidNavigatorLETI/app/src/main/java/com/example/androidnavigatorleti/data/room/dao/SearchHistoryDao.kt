package com.example.androidnavigatorleti.data.room.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.androidnavigatorleti.data.room.tables.DbSearchHistoryItem

interface SearchHistoryDao {

    @Query("SELECT * FROM DbSearchHistoryItem")
    suspend fun getSearchHistory(): List<DbSearchHistoryItem>

    @Insert
    suspend fun addSearchHistoryItem(vararg itemDb: DbSearchHistoryItem)

    @Delete
    suspend fun deleteSearchHistoryItem(itemDb: DbSearchHistoryItem)

    @Query("SELECT id FROM DbSearchHistoryItem WHERE place_name = (:place_name)")
    suspend fun getSearchHistoryItemId(place_name: String) : Int
}