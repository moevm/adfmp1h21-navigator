package com.example.androidnavigatorleti.di

import com.example.androidnavigatorleti.data.datasources.LocalDataSource
import com.example.androidnavigatorleti.data.datasources.RemoteDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object DataSourcesModule {

    val module = module {
        single { LocalDataSource(androidContext(), get(), get()) }
        single { RemoteDataSource(androidContext(), get()) }
    }
}