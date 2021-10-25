package com.example.androidnavigatorleti.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.androidnavigatorleti.data.room.tables.DatabaseSearchHistoryItem
import com.example.androidnavigatorleti.data.room.dao.UserDao
import com.example.androidnavigatorleti.data.room.UserDatabase.Companion.DB_VERSION
import com.example.androidnavigatorleti.data.room.dao.SearchHistoryDao
import com.example.androidnavigatorleti.data.room.tables.DatabaseUserInfo
import com.example.androidnavigatorleti.data.room.tables.DatabaseUserLocation

@Database(
    entities = [
        DatabaseUserInfo::class,
        DatabaseUserLocation::class,
        DatabaseSearchHistoryItem::class
               ],
    version = DB_VERSION
)
abstract class UserDatabase : RoomDatabase() {

    companion object {

        private const val DATABASE_NAME = "NavigatorDatabase"
        const val DB_VERSION = 7

        fun provideDatabase(context: Context): UserDatabase {
            return Room
                .databaseBuilder(
                    context,
                    UserDatabase::class.java,
                    DATABASE_NAME
                )
                .build()
        }
    }

    abstract fun userDao(): UserDao

    abstract fun searchHistoryDao(): SearchHistoryDao
}