package com.example.androidnavigatorleti.data.repository

import com.example.androidnavigatorleti.data.datasources.LocalDataSource
import com.example.androidnavigatorleti.data.datasources.RemoteDataSource
import com.example.androidnavigatorleti.data.room.tables.DbUserInfo
import com.example.androidnavigatorleti.domain.repository.UserRepository

class UserRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
): UserRepository {

    override suspend fun getUserOrNull(): DbUserInfo? = localDataSource.getUserOrNull()

    override suspend fun insertUser(infoDb: DbUserInfo) {
        localDataSource.insertUser(infoDb)
    }
}