package com.app.qrscanner

import android.app.Application
import com.app.qrscanner.data.local.AppDatabase
import com.app.qrscanner.di.createAppModule
import com.chibatching.kotpref.Kotpref
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application(){
    override fun onCreate() {
        super.onCreate()
        Kotpref.init(this)
        initRoom()
        initKoin()
    }

    private fun initRoom(){
        AppDatabase.build(applicationContext)
    }

    private fun initKoin(){
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                listOf(
                    createAppModule()
                )
            )
        }
    }
}