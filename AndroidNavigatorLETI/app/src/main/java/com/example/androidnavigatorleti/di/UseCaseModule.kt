package com.example.androidnavigatorleti.di

import com.example.androidnavigatorleti.domain.use_case.*
import org.koin.dsl.module

object UseCaseModule {

    val module = module {
        single { TrafficLightsUseCase(get()) }
        single { AddSearchHistoryUseCase(get()) }
        single { GetSearchHistoryUseCase(get()) }
        single { GetUserUseCase(get()) }
        single { InsertUserUseCase(get()) }
        single { DeleteSearchHistoryUseCase(get()) }
    }
}