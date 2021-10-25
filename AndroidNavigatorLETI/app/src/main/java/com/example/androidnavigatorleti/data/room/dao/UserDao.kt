package com.example.androidnavigatorleti.data.room.dao

import androidx.room.*
import com.example.androidnavigatorleti.data.room.tables.UserInfo

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
}