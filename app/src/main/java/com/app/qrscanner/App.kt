package com.app.qrscanner

import android.app.Application
import com.app.qrscanner.data.local.AppDatabase
import com.app.qrscanner.di.createAppModule
import com.chibatching.kotpref.Kotpref
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.yariksoffice.lingver.Lingver
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.net.URL


class App : Application(){
    override fun onCreate() {
        super.onCreate()
        Kotpref.init(this)
        Lingver.init(this, "english")
        initRoom()
        initKoin()

        initBarCodeReader()
    }

    private fun initBarCodeReader() {
        val options =
            FirebaseVisionBarcodeDetectorOptions.Builder()
                .build()
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