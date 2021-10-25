package com.example.androidnavigatorleti.data.room.dao

import androidx.room.*
import com.example.androidnavigatorleti.data.room.tables.DatabaseSearchHistoryItem

@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM DatabaseSearchHistoryItem")
    fun getSearchHistory(): List<DatabaseSearchHistoryItem>

    @Insert
    fun addSearchHistoryItem(vararg itemDatabase: DatabaseSearchHistoryItem)

    @Delete
    fun deleteSearchHistoryItem(itemDatabase: DatabaseSearchHistoryItem)

    @Query("SELECT id FROM DatabaseSearchHistoryItem WHERE place_name = (:place_name)")
    fun getSearchHistoryItemId(place_name: String) : Int
}