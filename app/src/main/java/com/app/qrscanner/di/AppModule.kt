package com.app.qrscanner.di

import com.app.qrscanner.data.CodesRepository
import com.app.qrscanner.data.local.AppDatabase
import org.koin.dsl.module
import ru.terrakok.cicerone.Cicerone

fun createAppModule() = module {
    val cicerone = Cicerone.create()
    single { cicerone.router }
    single { cicerone.navigatorHolder }
    single { AppDatabase.build(get()) }
    single { CodesRepository(get()) }

}