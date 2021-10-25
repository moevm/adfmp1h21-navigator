package com.example.androidnavigatorleti.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.androidnavigatorleti.data.room.tables.SearchHistoryItem
import com.example.androidnavigatorleti.data.room.dao.UserDao
import com.example.androidnavigatorleti.data.room.tables.UserInfo
import com.example.androidnavigatorleti.data.domain.UserLocation
import com.example.androidnavigatorleti.data.room.UserDatabase.Companion.DB_VERSION
import com.example.androidnavigatorleti.data.room.dao.SearchHistoryDao

@Database(
    entities = [
        UserInfo::class,
        UserLocation::class,
        SearchHistoryItem::class
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