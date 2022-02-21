package com.example.passengerapp

import android.app.Application

import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.util.logging.Level


class PassengerApp: Application() {

    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin{
            androidContext(this@PassengerApp)
            modules(appModule)
        }
    }

}