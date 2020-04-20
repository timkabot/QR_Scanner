package com.app.qrscanner

import android.app.Application
import com.app.qrscanner.data.local.AppDatabase
import com.app.qrscanner.di.createAppModule
import com.chibatching.kotpref.Kotpref
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.yariksoffice.lingver.Lingver
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.net.URL
import java.util.*


class App : Application(){
    override fun onCreate() {
        super.onCreate()
        Kotpref.init(this)
        Lingver.init(this, "english")
        initRoom()
        initKoin()

        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(Arrays.asList("4E6DEAA92CE1C5B77ABD737D7732711B"))
                .build()
        )
        MobileAds.initialize(this)

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