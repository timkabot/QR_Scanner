package com.app.qrscanner

import android.net.Uri
import com.app.qrscanner.domain.entities.CodeType
import com.app.qrscanner.domain.entities.SerializableResult
import com.app.qrscanner.presentation.createQr.CreateCodeFragment
import com.app.qrscanner.presentation.createQr.calendar.CreateCalendarCodeFragment
import com.app.qrscanner.presentation.createQr.card.CreateCardCodeFragment
import com.app.qrscanner.presentation.createQr.contact.CreateContactFragment
import com.app.qrscanner.presentation.createQr.facebook.CreateFacebookCodeFragment
import com.app.qrscanner.presentation.createQr.instagram.CreateInstagramCodeFragment
import com.app.qrscanner.presentation.createQr.mail.CreateMailCodeFragment
import com.app.qrscanner.presentation.createQr.paypal.CreatePaypalCodeFragment
import com.app.qrscanner.presentation.createQr.phone.CreatePhoneCodeFragment
import com.app.qrscanner.presentation.createQr.sms.CreateSmsCodeFragment
import com.app.qrscanner.presentation.createQr.spotify.CreateSpotifyCodeFragment
import com.app.qrscanner.presentation.createQr.text.CreateTextCodeFragment
import com.app.qrscanner.presentation.createQr.twitter.CreateTwitterCodeFragment
import com.app.qrscanner.presentation.createQr.viber.CreateViberCodeFragment
import com.app.qrscanner.presentation.createQr.whatsapp.CreateWhatsAppCodeFragment
import com.app.qrscanner.presentation.createQr.wifi.CreateWifiCodeFragment
import com.app.qrscanner.presentation.createQr.youtube.CreateYoutubeCodeFragment
import com.app.qrscanner.presentation.cropImage.CropFragment
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

    class CropScreen(private val imageUri: Uri) : SupportAppScreen() {
        override fun getFragment() = CropFragment.create(imageUri)
    }

    object CreatedBarCodesSubScreen : SupportAppScreen() {
        override fun getFragment() = CreatedCodesHistory()
    }

    object CreateQRScreen : SupportAppScreen() {
        override fun getFragment() = CreateCodeFragment()
    }
    object CreateWifiQrScreen : SupportAppScreen() {
        override fun getFragment() = CreateWifiCodeFragment()
    }

    object CreateFacebookQrScreen : SupportAppScreen() {
        override fun getFragment() = CreateFacebookCodeFragment()
    }

    object CreateWhatsAppQrScreen : SupportAppScreen() {
        override fun getFragment() = CreateWhatsAppCodeFragment()
    }

    object CreateContactQrScreen : SupportAppScreen() {
        override fun getFragment() = CreateContactFragment()
    }

    object CreateMailQrScreen : SupportAppScreen() {
        override fun getFragment() = CreateMailCodeFragment()
    }


    object CreateYoutubeQrScreen : SupportAppScreen() {
        override fun getFragment() = CreateYoutubeCodeFragment()
    }

    object CreatePaypalQrScreen : SupportAppScreen() {
        override fun getFragment() = CreatePaypalCodeFragment()
    }

    object CreateCardQrScreen : SupportAppScreen() {
        override fun getFragment() = CreateCardCodeFragment()
    }

    object CreateViberQrScreen : SupportAppScreen() {
        override fun getFragment() = CreateViberCodeFragment()
    }

    object CreateInstagramQrScreen : SupportAppScreen() {
        override fun getFragment() = CreateInstagramCodeFragment()
    }

    object CreateTwitterQrScreen : SupportAppScreen() {
        override fun getFragment() = CreateTwitterCodeFragment()
    }

    object CreateSpotifyQrScreen : SupportAppScreen() {
        override fun getFragment() = CreateSpotifyCodeFragment()
    }
    object CreateTextQrScreen : SupportAppScreen() {
        override fun getFragment() = CreateTextCodeFragment()
    }
    object CreatePhoneQrScreen : SupportAppScreen() {
        override fun getFragment() = CreatePhoneCodeFragment()
    }
    object CreateSmsQrScreen : SupportAppScreen() {
        override fun getFragment() = CreateSmsCodeFragment()
    }
    object CreateCalendarQrScreen : SupportAppScreen() {
        override fun getFragment() = CreateCalendarCodeFragment()
    }

    class ShowCreatedQRScreen(private val value: String, val codeType: CodeType? = null) :
        SupportAppScreen() {
        override fun getFragment() = ShowCreatedCodeFragment.create(value, codeType)
    }

    object ShowScannedQRScreen :
        SupportAppScreen() {
        override fun getFragment() = ShowScannedCodeFragment()
    }

    object SettingsScreen : SupportAppScreen() {
        override fun getFragment() = SettingsFragment()
    }
}