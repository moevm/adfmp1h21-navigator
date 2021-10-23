package com.example.androidnavigatorleti

import android.app.Application
import androidx.room.Room
import com.example.androidnavigatorleti.data.UserDao
import com.example.androidnavigatorleti.data.UserDatabase

class NavigatorApp : Application() {

    override fun onCreate() {
        super.onCreate()

        db = Room.databaseBuilder(
            baseContext,
            UserDatabase::class.java,
            "NavigatorDatabase"
        ).allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
        userDao = db.userDao()
    }

    companion object {
        lateinit var db: UserDatabase
        lateinit var userDao: UserDao
    }
}