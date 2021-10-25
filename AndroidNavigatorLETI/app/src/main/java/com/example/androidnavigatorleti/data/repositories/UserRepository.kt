package com.example.androidnavigatorleti.data.repositories

import com.example.androidnavigatorleti.data.room.tables.SearchHistoryItem
import com.example.androidnavigatorleti.data.room.tables.UserInfo

interface UserRepository {

    fun getUserOrNull(): UserInfo?

    fun getSearchHistory(): List<SearchHistoryItem>

    fun insertUser(info: UserInfo)

    fun deleteHistoryItem(item: SearchHistoryItem)

    fun addSearchHistoryItem(item: SearchHistoryItem)
}