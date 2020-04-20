package com.app.qrscanner.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.qrscanner.domain.interactors.AndroidServicesInteractor
import com.app.qrscanner.domain.interactors.CodeTypeInteractor
import com.app.qrscanner.domain.interactors.DatabaseInteractor
import com.app.qrscanner.domain.interactors.ParsedResultInteractor
import com.google.zxing.Result
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.support.SupportAppScreen

class MainViewModel(
    val databaseInteractor: DatabaseInteractor,
    val router: Router,
    val androidServicesInteractor: AndroidServicesInteractor,
    val parsedResultInteractor: ParsedResultInteractor,
    val codeTypeInteractor: CodeTypeInteractor
) : ViewModel() {
    var lastScannedResult: Result? = null
    var g = 4
    var transitionCount = 0
    init {
        lastScannedResult = null
        println("VieModel INIT")
    }

    val adsEvent = MutableLiveData<Boolean>()

    fun goToScreen(screen: SupportAppScreen){
        transitionCount++
        println(transitionCount)
        if(transitionCount%4 == 0) adsEvent.postValue(true)
        router.navigateTo(screen)
    }

}