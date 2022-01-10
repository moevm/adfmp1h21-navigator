package com.example.androidnavigatorleti.data.room.dao

import androidx.room.*
import com.example.androidnavigatorleti.data.room.tables.DbUserInfo

@Dao
interface UserDao {

    @Query("SELECT * FROM DbUserInfo")
    suspend fun getUser(): DbUserInfo?

    @Insert
    suspend fun insertUser(vararg dbUser: DbUserInfo)

    @Update
    suspend fun updateUser(vararg dbUser: DbUserInfo)

    @Delete
    suspend fun deleteUser(dbUser: DbUserInfo)
}