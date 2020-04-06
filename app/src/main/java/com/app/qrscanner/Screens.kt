package com.app.qrscanner

import com.app.qrscanner.ui.history.HistoryFragment
import com.app.qrscanner.ui.history.createdCodesHistory.CreatedCodesHistory
import com.app.qrscanner.ui.history.scannedCodesHistory.ScannedCodesFragment
import com.app.qrscanner.ui.scanQr.ScanQrFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {
    object HistoryScreen : SupportAppScreen(){
        override fun getFragment() = HistoryFragment()
    }
    object ScanQRScreen : SupportAppScreen() {
        override fun getFragment() = ScanQrFragment()
    }

    object ScannedBarCodesSubScreen : SupportAppScreen(){
        override fun getFragment() = ScannedCodesFragment()
    }

    object CreatedBarCodesSubScreen : SupportAppScreen(){
        override fun getFragment() = CreatedCodesHistory()
    }

    //object CreateQRScreen()
}