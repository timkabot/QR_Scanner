package com.app.qrscanner.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.app.qrscanner.R
import com.app.qrscanner.domain.entities.CodeType
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.result.AddressBookParsedResult
import com.google.zxing.client.result.EmailAddressParsedResult
import com.google.zxing.client.result.ParsedResult
import com.google.zxing.client.result.ParsedResultType
import com.google.zxing.qrcode.QRCodeWriter
import java.net.URL
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun String.showToast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()

}


fun getEmailInfo(email: EmailAddressParsedResult): String {
    var info = ""
    with(email) {
        whenNotNull(tos) { if (tos.isNotEmpty()) info += "Получатель: ${tos.contentToString()}\n" }
        whenNotNull(subject) { if (subject.isNotEmpty()) info += "Тема: ${subject}\n" }
        whenNotNull(body) { if (body.isNotEmpty()) info += "Контент: ${body}\n" }
    }
    return info
}

fun getAddressBookInfo(addressBook: AddressBookParsedResult): String {
    var info = ""
    with(addressBook) {
        whenNotNull(names) { if (names.isNotEmpty()) info += "Имя: ${names.contentToString()}\n" }
        whenNotNull(nicknames) { if (nicknames.isNotEmpty()) info += "Прозвище: ${nicknames.contentToString()}\n" }
        whenNotNull(pronunciation) { if (pronunciation.isNotEmpty()) info += "Произношение: ${pronunciation}\n" }
        whenNotNull(phoneNumbers) { if (phoneNumbers.isNotEmpty()) info += "Номер: ${phoneNumbers.contentToString()}\n" }
        whenNotNull(phoneTypes) { if (phoneTypes.isNotEmpty()) info += "Тип номера: ${phoneTypes.contentToString()}\n" }
        whenNotNull(emails) { if (emails.isNotEmpty()) info += "Email: ${emails.contentToString()}\n" }
        whenNotNull(emailTypes) { if (emailTypes.isNotEmpty()) info += "Тип email: ${emailTypes.contentToString()}\n" }
        whenNotNull(geo) { if (geo.isNotEmpty()) info += "Геопозиция : ${geo.contentToString()}\n" }

        whenNotNull(instantMessenger) { if (instantMessenger.isNotEmpty()) info += "Связь: ${instantMessenger}\n" }
        whenNotNull(note) { if (note.isNotEmpty()) info += "Заметки: ${note}\n" }
        whenNotNull(birthday) { if (birthday.isNotEmpty()) info += "День рождения: ${birthday}\n" }
        whenNotNull(title) { if (title.isNotEmpty()) info += "Заголовок: ${title}\n" }

        whenNotNull(addresses) { if (addresses.isNotEmpty()) info += "Адреса: ${addresses.contentToString()}\n" }

    }
    return info
}

inline fun <T : Any, R> whenNotNull(input: T?, callback: (T) -> R): R? {
    return input?.let(callback)
}

fun getSiteType(link: String): CodeType {
    println(link)

    if(link.startsWith("instagram")) return CodeType.INSTAGRAM
    if(link.startsWith("twitter")) return CodeType.TWITTER
    if(link.startsWith("spotify")) return CodeType.INSTAGRAM

    try {
        val url = URL(link)
        val host = url.host
        val profile = url.path
        println(host)
        host.toLowerCase().also {
            if (it.contains("twitter")) return CodeType.TWITTER
            if (it.contains("instagram")) return CodeType.INSTAGRAM
            if (it.contains("youtube")) return CodeType.YOUTUBE
            if (it.contains("youtu.be")) return CodeType.YOUTUBE

            if (it.contains("spotify")) return CodeType.SPOTIFY
        }
    } catch (e: Exception) {
    }

    return CodeType.URI
}

fun getCodeTypeForParsedResult(parsedResult: ParsedResult): CodeType {
    return when (parsedResult.type) {
        ParsedResultType.ADDRESSBOOK -> CodeType.ADDRESSBOOK
        ParsedResultType.EMAIL_ADDRESS -> CodeType.EMAIL_ADDRESS
        ParsedResultType.PRODUCT -> CodeType.VIN
        ParsedResultType.GEO -> CodeType.GEO
        ParsedResultType.URI -> getSiteType(parsedResult.displayResult)
        ParsedResultType.TEXT -> CodeType.TEXT
        ParsedResultType.TEL -> CodeType.TEL
        ParsedResultType.SMS -> CodeType.SMS
        ParsedResultType.CALENDAR -> CodeType.CALENDAR
        ParsedResultType.WIFI -> CodeType.WIFI
        ParsedResultType.ISBN -> CodeType.TEXT
        ParsedResultType.VIN -> CodeType.VIN
        else -> CodeType.VIN
    }
}

fun getImageForCodeType(codeType: CodeType): Int {
    return when (codeType) {
        CodeType.EMAIL_ADDRESS -> R.drawable.mail
        CodeType.FACEBOOK -> R.drawable.fb
        CodeType.YOUTUBE -> R.drawable.youtube
        CodeType.WHATSAPP -> R.drawable.whatsapp
        CodeType.CARD -> R.drawable.bcard
        CodeType.VIBER -> R.drawable.viber
        CodeType.INSTAGRAM -> R.drawable.instagram
        CodeType.WIFI -> R.drawable.wifi
        CodeType.TWITTER -> R.drawable.twitter
        CodeType.CALENDAR -> R.drawable.calendar
        CodeType.SPOTIFY -> R.drawable.spotify
        CodeType.SMS -> R.drawable.sms
        CodeType.TEL -> R.drawable.phone
        CodeType.TEXT -> R.drawable.text
        CodeType.ADDRESSBOOK -> R.drawable.contacts
        CodeType.PAYPAL -> R.drawable.paypal
        else -> R.drawable.url
    }
}

fun getNameForCodeType(codeType: CodeType) =
    when (codeType) {
        CodeType.EMAIL_ADDRESS -> "Почта"
        CodeType.FACEBOOK -> "Facebook"
        CodeType.YOUTUBE -> "Youtube"
        CodeType.WHATSAPP -> "Whatsapp"
        CodeType.CARD -> "Моя карточка"
        CodeType.VIBER -> "Viber"
        CodeType.INSTAGRAM -> "Instagram"
        CodeType.WIFI -> "WI-FI"
        CodeType.TWITTER -> "Twitter"
        CodeType.CALENDAR -> "Календарь"
        CodeType.SPOTIFY -> "Spotify"
        CodeType.SMS -> "СМС"
        CodeType.TEL -> "Телефон"
        CodeType.TEXT -> "Текст"
        CodeType.ADDRESSBOOK -> "Контакт"
        CodeType.PAYPAL -> "PayPal"
        else -> "URI"

    }

fun createQR(value: String, width: Int = 512, height: Int = 512): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(value, BarcodeFormat.QR_CODE, width, height)
    val bitmap = Bitmap.createBitmap(bitMatrix.width, bitMatrix.height, Bitmap.Config.RGB_565)

    for (x in 0 until bitMatrix.width) {
        for (y in 0 until height) {
            bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
        }
    }
    return bitmap
}

inline fun <reified T> extra(
    key: String,
    defaultValue: T? = null
): ReadWriteProperty<Activity, T> =
    BundleExtractorDelegate { thisRef ->
        extractFromBundle(
            bundle = thisRef.intent?.extras,
            key = key,
            defaultValue = defaultValue
        )
    }

inline fun <reified T> argument(
    key: String,
    defaultValue: T? = null
): ReadWriteProperty<Fragment, T> =
    BundleExtractorDelegate { thisRef ->
        extractFromBundle(
            bundle = thisRef.arguments,
            key = key,
            defaultValue = defaultValue
        )
    }

inline fun <reified T> extractFromBundle(
    bundle: Bundle?,
    key: String,
    defaultValue: T? = null
): T {
    val result = bundle?.get(key) ?: defaultValue
    if (result != null && result !is T) {
        throw ClassCastException("Property $key has different class type")
    }
    return result as T
}

class BundleExtractorDelegate<R, T>(private val initializer: (R) -> T) : ReadWriteProperty<R, T> {

    private object EMPTY

    private var value: Any? = EMPTY

    override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        this.value = value
    }

    override fun getValue(thisRef: R, property: KProperty<*>): T {
        if (value == EMPTY) {
            value = initializer(thisRef)
        }
        @Suppress("UNCHECKED_CAST")
        return value as T
    }
}