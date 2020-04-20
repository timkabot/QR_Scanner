package com.app.qrscanner.di

import com.app.qrscanner.data.CodesRepository
import com.app.qrscanner.data.local.AppDatabase
import com.app.qrscanner.domain.interactors.*
import com.app.qrscanner.presentation.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.terrakok.cicerone.Cicerone

fun createAppModule() = module {
    val cicerone = Cicerone.create()
    single { cicerone.router }
    single { cicerone.navigatorHolder }
    single { AppDatabase.build(get()) }
    single { CodesRepository(get()) }
    single { CodeTypeInteractor() }
    single { DatabaseInteractor(get()) }
    single { ParsedResultInteractor(get()) }
    single { AndroidServicesInteractor(get()) }
    single { MainViewModel(get(),get(), get(), get(), get()) }
    factory { MyResultParser(get()) }
}