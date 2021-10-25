package com.example.androidnavigatorleti.data.room.dao

import androidx.room.*
import com.example.androidnavigatorleti.data.room.tables.DatabaseUserInfo

@Dao
interface UserDao {

    @Query("SELECT * FROM DatabaseUserInfo")
    fun getUser(): DatabaseUserInfo?

    @Insert
    fun insertUser(vararg user: DatabaseUserInfo)

    @Update
    fun updateUser(vararg user: DatabaseUserInfo)

    @Delete
    fun deleteUser(user: DatabaseUserInfo)
}