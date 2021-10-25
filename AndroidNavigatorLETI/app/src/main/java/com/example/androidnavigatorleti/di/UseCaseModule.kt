package com.example.androidnavigatorleti.di

import com.example.androidnavigatorleti.uc.UserUc
import org.koin.dsl.module

object UseCaseModule {

    val module = module {
        single { UserUc(get()) }
    }
}