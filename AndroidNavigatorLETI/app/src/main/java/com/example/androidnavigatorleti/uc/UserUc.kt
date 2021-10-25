package com.example.androidnavigatorleti.uc

import com.example.androidnavigatorleti.data.domain.SearchHistoryItem
import com.example.androidnavigatorleti.data.domain.UserInfo
import com.example.androidnavigatorleti.data.repositories.UserRepository

class UserUc(private val userRepository: UserRepository) {

    fun getUserOrNull(): UserInfo? = userRepository.getUserOrNull()

    fun getSearchHistory(): List<SearchHistoryItem> = userRepository.getSearchHistory()

    fun insertUser(info: UserInfo) {
        userRepository.insertUser(info)
    }

    fun deleteUser(itemDatabase: SearchHistoryItem) {
        userRepository.deleteHistoryItem(itemDatabase)
    }

    fun addSearchHistoryItem(itemDatabase: SearchHistoryItem) {
        userRepository.addSearchHistoryItem(itemDatabase)
    }
}