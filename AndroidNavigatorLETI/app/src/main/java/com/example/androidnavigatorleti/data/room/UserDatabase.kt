package com.example.androidnavigatorleti.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.androidnavigatorleti.data.room.dao.SearchHistoryDao
import com.example.androidnavigatorleti.data.room.tables.DbSearchHistoryItem
import com.example.androidnavigatorleti.data.room.dao.UserDao
import com.example.androidnavigatorleti.data.room.tables.DbUserInfo
import com.example.androidnavigatorleti.data.room.tables.DbUserLocation

@Database(entities = [DbUserInfo::class, DbUserLocation::class, DbSearchHistoryItem::class], version = 6)
abstract class UserDatabase : RoomDatabase() {

    companion object {

        private const val DATABASE_NAME = "NavigatorDatabase"

        fun provideDatabase(context: Context): UserDatabase {
            return Room
                .databaseBuilder(
                    context,
                    UserDatabase::class.java,
                    DATABASE_NAME
                )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun userDao(): UserDao

    abstract fun searchHistoryDao(): SearchHistoryDao
}