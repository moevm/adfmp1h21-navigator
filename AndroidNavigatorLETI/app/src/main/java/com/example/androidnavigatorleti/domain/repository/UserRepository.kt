package com.example.androidnavigatorleti.domain.repository

import com.example.androidnavigatorleti.data.room.tables.DbUserInfo

interface UserRepository {

    suspend fun getUserOrNull(): DbUserInfo?

    suspend fun insertUser(infoDb: DbUserInfo)
}