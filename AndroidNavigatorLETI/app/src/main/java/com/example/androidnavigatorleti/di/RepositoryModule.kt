package com.example.androidnavigatorleti.di

import com.example.androidnavigatorleti.data.repository.SearchHistoryRepositoryImpl
import com.example.androidnavigatorleti.data.repository.TrafficLightsRepositoryImpl
import com.example.androidnavigatorleti.data.repository.UserRepositoryImpl
import com.example.androidnavigatorleti.domain.repository.SearchHistoryRepository
import com.example.androidnavigatorleti.domain.repository.TrafficLightsRepository
import com.example.androidnavigatorleti.domain.repository.UserRepository
import org.koin.dsl.module


object RepositoryModule {

    val module = module {
        single<TrafficLightsRepository> { TrafficLightsRepositoryImpl(get(), get()) }
        single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get(), get()) }
        single<UserRepository> { UserRepositoryImpl(get(), get()) }
    }
}