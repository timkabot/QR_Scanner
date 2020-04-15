package com.app.qrscanner

import com.app.qrscanner.domain.entities.SerializableResult
import com.app.qrscanner.presentation.createQr.CreateCodeFragment
import com.app.qrscanner.presentation.history.HistoryFragment
import com.app.qrscanner.presentation.history.createdCodesHistory.CreatedCodesHistory
import com.app.qrscanner.presentation.history.scannedCodesHistory.ScannedCodesFragment
import com.app.qrscanner.presentation.scanQr.ScanQrFragment
import com.app.qrscanner.presentation.settings.SettingsFragment
import com.app.qrscanner.presentation.showQr.ShowCreatedCodeFragment
import com.app.qrscanner.presentation.showQr.ShowScannedCodeFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {
    object HistoryScreen : SupportAppScreen() {
        override fun getFragment() = HistoryFragment()
    }

    object ScanQRScreen : SupportAppScreen() {
        override fun getFragment() = ScanQrFragment()
    }

    object ScannedBarCodesSubScreen : SupportAppScreen() {
        override fun getFragment() = ScannedCodesFragment()
    }

    object CreatedBarCodesSubScreen : SupportAppScreen() {
        override fun getFragment() = CreatedCodesHistory()
    }

    object CreateQRScreen : SupportAppScreen() {
        override fun getFragment() = CreateCodeFragment()
    }

    class ShowCreatedQRScreen(private val value: String, private val result: SerializableResult) :
        SupportAppScreen() {
        override fun getFragment() = ShowCreatedCodeFragment.create(value, result)
    }

    class ShowScannedQRScreen(private val value: String, private val result: SerializableResult) :
        SupportAppScreen() {
        override fun getFragment() = ShowScannedCodeFragment.create(value, result)
    }

    object SettingsScreen : SupportAppScreen() {
        override fun getFragment() = SettingsFragment()
    }
}