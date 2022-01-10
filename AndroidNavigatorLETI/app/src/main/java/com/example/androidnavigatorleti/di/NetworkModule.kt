package com.example.androidnavigatorleti.di

import com.example.androidnavigatorleti.data.remote.TrafficLightApi
import com.example.androidnavigatorleti.data.remote.retrofit.RetrofitFactory
import org.koin.dsl.module

object NetworkModule {

    val module = module {
        single {
            RetrofitFactory.getServiceInstance(
                TrafficLightApi::class.java,
                "/",
            )
        }
    }
}