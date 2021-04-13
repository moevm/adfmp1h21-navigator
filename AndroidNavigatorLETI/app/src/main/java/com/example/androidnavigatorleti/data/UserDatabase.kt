package com.example.androidnavigatorleti.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserInfo::class, UserLocation::class, SearchHistoryItem::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}