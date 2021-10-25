package com.example.androidnavigatorleti.data.repositories

import com.example.androidnavigatorleti.data.domain.SearchHistoryItem
import com.example.androidnavigatorleti.data.domain.UserInfo

interface UserRepository {

    fun getUserOrNull(): UserInfo?

    fun getSearchHistory(): List<SearchHistoryItem>

    fun insertUser(info: UserInfo)

    fun deleteHistoryItem(itemDatabase: SearchHistoryItem)

    fun addSearchHistoryItem(itemDatabase: SearchHistoryItem)
}