package com.example.androidnavigatorleti.data

import androidx.room.*

@Dao
interface UserDao {

    @Query("SELECT * FROM UserInfo")
    fun getUser(): UserInfo

    @Insert
    fun insertUser(vararg user: UserInfo)

    @Update
    fun updateUser(vararg user: UserInfo)

    @Delete
    fun deleteUser(user: UserInfo)

    @Query("SELECT * FROM UserLocation")
    fun getLocation(): UserLocation

    @Insert
    fun insertLocation(vararg location: UserLocation)

    @Update
    fun updateLocation(vararg location: UserLocation)

    @Delete
    fun deleteLocation(location: UserLocation)
}