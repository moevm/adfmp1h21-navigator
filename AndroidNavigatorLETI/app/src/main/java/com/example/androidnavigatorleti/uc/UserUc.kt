package com.example.androidnavigatorleti.uc

import com.example.androidnavigatorleti.data.repositories.UserRepository
import com.example.androidnavigatorleti.data.room.tables.SearchHistoryItem
import com.example.androidnavigatorleti.data.room.tables.UserInfo

class UserUc(private val userRepository: UserRepository) {

    fun getUserOrNull(): UserInfo? = userRepository.getUserOrNull()

    fun getSearchHistory(): List<SearchHistoryItem> = userRepository.getSearchHistory()

    fun insertUser(info: UserInfo) {
        userRepository.insertUser(info)
    }

    fun deleteUser(item: SearchHistoryItem) {
        userRepository.deleteHistoryItem(item)
    }

    fun addSearchHistoryItem(item: SearchHistoryItem) {
        userRepository.addSearchHistoryItem(item)
    }
}