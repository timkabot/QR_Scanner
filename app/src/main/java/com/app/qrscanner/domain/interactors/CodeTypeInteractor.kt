package com.app.qrscanner.domain.interactors

import android.content.Context
import com.app.qrscanner.R
import com.app.qrscanner.domain.entities.CodeType
import com.app.qrscanner.domain.entities.CodeType.*
import java.net.URL
import java.util.*

class CodeTypeInteractor {
    fun getImageForCodeType(codeType: CodeType): Int {
        return when (codeType) {
            EMAIL_ADDRESS -> R.drawable.mail
            FACEBOOK -> R.drawable.fb
            YOUTUBE -> R.drawable.youtube
            WHATSAPP -> R.drawable.whatsapp
            CARD -> R.drawable.bcard
            VIBER -> R.drawable.viber
            INSTAGRAM -> R.drawable.instagram
            WIFI -> R.drawable.wifi
            TWITTER -> R.drawable.twitter
            CALENDAR -> R.drawable.calendar
            SPOTIFY -> R.drawable.spotify
            SMS -> R.drawable.sms
            TEL -> R.drawable.phone
            TEXT -> R.drawable.text
            ADDRESSBOOK -> R.drawable.contacts2
            PAYPAL -> R.drawable.paypal
            URI -> R.drawable.url
            VIN -> R.drawable.autocopy
            GEO -> R.drawable.eye
        }
    }

    fun getNameForCodeType(codeType: CodeType, context: Context) =
        with(context) {
            when (codeType) {
                EMAIL_ADDRESS -> getString(R.string.mail)
                FACEBOOK -> getString(R.string.facebook)
                YOUTUBE -> getString(R.string.youtube)
                WHATSAPP -> getString(R.string.whatsapp)
                CARD -> getString(R.string.myCard)
                VIBER -> getString(R.string.viber)
                INSTAGRAM -> getString(R.string.instagram)
                WIFI -> getString(R.string.wifi)
                TWITTER -> getString(R.string.twitter)
                CALENDAR -> getString(R.string.calendar)
                SPOTIFY -> getString(R.string.spotify)
                SMS -> getString(R.string.sms)
                TEL -> getString(R.string.phone)
                TEXT -> getString(R.string.text)
                ADDRESSBOOK -> getString(R.string.contact)
                PAYPAL -> getString(R.string.paypal)
                else -> getString(R.string.uri)
            }
        }

    fun getSiteType(link: String): CodeType {
        with(link) {
            if (startsWith("instagram")) return INSTAGRAM
            if (startsWith("twitter")) return TWITTER
            if (startsWith("fb")) return FACEBOOK
            if (startsWith("spotify")) return SPOTIFY
            if (startsWith("whatsapp")) return WHATSAPP

        }

        try {
            val host = URL(link).host
            host.toLowerCase(Locale.getDefault()).apply {
                if (contains("twitter")) return TWITTER
                if (contains("instagram")) return INSTAGRAM
                if (contains("youtube")) return YOUTUBE
                if (contains("youtu.be")) return YOUTUBE
                if (contains("spotify")) return SPOTIFY
                if (contains("facebook")) return FACEBOOK
                if (contains("whatsapp")) return FACEBOOK

            }
        } catch (e: Exception) {
        }

        return URI
    }
}