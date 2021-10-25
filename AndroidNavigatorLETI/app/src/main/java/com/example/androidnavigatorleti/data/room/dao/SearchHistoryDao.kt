package com.example.androidnavigatorleti.data.room.dao

import androidx.room.*
import com.example.androidnavigatorleti.data.room.tables.SearchHistoryItem

@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM SearchHistoryItem")
    fun getSearchHistory(): List<SearchHistoryItem>

    @Insert
    fun addSearchHistoryItem(vararg item: SearchHistoryItem)

    @Delete
    fun deleteSearchHistoryItem(item: SearchHistoryItem)

    @Query("SELECT id FROM SearchHistoryItem WHERE place_name = (:place_name)")
    fun getSearchHistoryItemId(place_name: String) : Int
}