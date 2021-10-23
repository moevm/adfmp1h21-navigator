package com.example.androidnavigatorleti.uc

import com.example.androidnavigatorleti.NavigatorApp
import com.example.androidnavigatorleti.data.SearchHistoryItem
import com.example.androidnavigatorleti.data.UserDao
import com.example.androidnavigatorleti.data.UserInfo

class UserUc {

    private val dao: UserDao = NavigatorApp.userDao

    fun getUserOrNull(): UserInfo? = dao.getUser()

    fun getSearchHistory(): List<SearchHistoryItem> = dao.getSearchHistory()

    fun insertUser(info: UserInfo) {
        dao.insertUser(info)
    }

    fun deleteItem(item: SearchHistoryItem) {
        dao.deleteSearchHistoryItem(item)
    }

    fun addSearchHistoryItem(item: SearchHistoryItem) {
        dao.addSearchHistoryItem(item)
    }
}