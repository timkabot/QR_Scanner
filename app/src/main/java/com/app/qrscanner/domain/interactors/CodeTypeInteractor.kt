package com.app.qrscanner.domain.interactors

import android.content.Context
import com.app.qrscanner.R
import com.app.qrscanner.domain.entities.CodeType
import com.app.qrscanner.domain.entities.CodeType.*
import com.app.qrscanner.domain.entities.MailSchema
import net.glxn.qrgen.core.scheme.Schema
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
            TEXT -> R.drawable.text_button
            ADDRESSBOOK -> R.drawable.contacts_button
            PAYPAL -> R.drawable.paypal
            URI -> R.drawable.url_button
            VIN -> R.drawable.autocopy_button
            GEO -> R.drawable.eye_button
        }
    }

    fun getCodeTypeForSchema(schema: Schema) : CodeType {
        if(schema is net.glxn.qrgen.core.scheme.SMS) return SMS
        if(schema is net.glxn.qrgen.core.scheme.VCard) return ADDRESSBOOK
        if(schema is net.glxn.qrgen.core.scheme.Url) return URI
        if(schema is MailSchema) return EMAIL_ADDRESS
        if(schema is net.glxn.qrgen.core.scheme.Telephone) return TEL
        if(schema is net.glxn.qrgen.core.scheme.Wifi) return WIFI
        if(schema is net.glxn.qrgen.core.scheme.ICal) return CALENDAR
        else return SMS
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

        with(link.toLowerCase()) {
            if (startsWith("instagram")) return INSTAGRAM
            if (startsWith("twitter")) return TWITTER
            if (startsWith("fb")) return FACEBOOK
            if (startsWith("facebook")) return FACEBOOK
            if (startsWith("spotify")) return SPOTIFY
            if (startsWith("whatsapp")) return WHATSAPP
            if (startsWith("viber")) return VIBER
            if (startsWith("paypal")) return PAYPAL
        }
        try {
            val host = URL(link).host
            host.toLowerCase().apply {
                if (contains("twitter")) return TWITTER
                if (contains("instagram")) return INSTAGRAM
                if (contains("youtube")) return YOUTUBE
                if (contains("youtu.be")) return YOUTUBE
                if (contains("spotify")) return SPOTIFY
                if (contains("facebook")) return FACEBOOK
                if (contains("whatsapp")) return WHATSAPP
                if (contains("viber")) return VIBER
                if (contains("paypal")) return PAYPAL
            }
        } catch (e: Exception) {
        }

        return URI
    }
}