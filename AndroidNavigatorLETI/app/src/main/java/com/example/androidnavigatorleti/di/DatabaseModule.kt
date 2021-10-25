package com.example.androidnavigatorleti.di

import com.example.androidnavigatorleti.data.room.UserDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object DatabaseModule {

    val module = module {
        single { UserDatabase.provideDatabase(androidContext()) }
    }
}