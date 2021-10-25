package com.example.androidnavigatorleti.di

import com.example.androidnavigatorleti.data.repositories.UserRepository
import com.example.androidnavigatorleti.data.repositories.UserRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object RepositoryModule {

    val module = module {
        single<UserRepository> { UserRepositoryImpl(androidContext(), get(), get()) }
    }
}