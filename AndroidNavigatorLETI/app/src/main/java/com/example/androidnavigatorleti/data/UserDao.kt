package com.example.androidnavigatorleti.data

import androidx.room.*

@Dao
interface UserDao {

    @Query("SELECT * FROM UserInfo")
    fun getUser(): UserInfo?

    @Insert
    fun insertUser(vararg user: UserInfo)

    @Update
    fun updateUser(vararg user: UserInfo)

    @Delete
    fun deleteUser(user: UserInfo)

    @Query("SELECT * FROM SearchHistoryItem")
    fun getSearchHistory(): List<SearchHistoryItem>

    @Insert
    fun addSearchHistoryItem(vararg item: SearchHistoryItem)

    @Delete
    fun deleteSearchHistoryItem(item: SearchHistoryItem)

    @Query("SELECT id FROM SearchHistoryItem WHERE place_name = (:place_name)")
    fun getSearchHistoryItemId(place_name: String) : Int
}