package com.app.qrscanner

import androidx.fragment.app.Fragment
import com.app.qrscanner.ui.createQr.CreateCodeFragment
import com.app.qrscanner.ui.history.HistoryFragment
import com.app.qrscanner.ui.history.createdCodesHistory.CreatedCodesHistory
import com.app.qrscanner.ui.history.scannedCodesHistory.ScannedCodesFragment
import com.app.qrscanner.ui.scanQr.ScanQrFragment
import com.app.qrscanner.ui.showQr.ShowCodeFragment
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

    object CreateQRScreen : SupportAppScreen(){
        override fun getFragment() = CreateCodeFragment()
    }

    class ShowQRScreen(val value: String) : SupportAppScreen(){
        override fun getFragment() = ShowCodeFragment.create(value)
    }
}