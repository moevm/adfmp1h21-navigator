package com.example.androidnavigatorleti.di

import com.example.androidnavigatorleti.data.datasources.LocalDataSource
import com.example.androidnavigatorleti.data.datasources.RemoteDataSource
import org.koin.dsl.module

object DataSourceModule {
    val module = module {
        single { LocalDataSource(get(), get()) }
        single { RemoteDataSource(get()) }
    }
}