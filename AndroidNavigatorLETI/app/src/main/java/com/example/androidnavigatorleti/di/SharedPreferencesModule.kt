package com.example.androidnavigatorleti.di

import com.example.androidnavigatorleti.data.preferences.SharedPreferencesManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object SharedPreferencesModule {

    val module = module {
        single { SharedPreferencesManager(androidContext()) }
    }
}