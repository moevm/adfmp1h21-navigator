package com.example.androidnavigatorleti

import android.app.Application
import com.example.androidnavigatorleti.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class NavigatorApp : Application() {

    override fun onCreate() {
        super.onCreate()

        //Инициализация DI
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@NavigatorApp)

            modules(
                SharedPreferencesModule.module,
                DatabaseModule.module,
                DataSourcesModule.module,
                RepositoryModule.module,
                UseCaseModule.module
            )
        }
    }
}