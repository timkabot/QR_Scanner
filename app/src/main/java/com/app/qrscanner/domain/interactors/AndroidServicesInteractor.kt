package com.app.qrscanner.domain.interactors

import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.net.wifi.WifiManager
import android.provider.ContactsContract
import android.provider.ContactsContract.Intents.Insert
import android.provider.Settings
import com.app.qrscanner.domain.entities.CodeType
import com.app.qrscanner.domain.entities.Contact
import com.app.qrscanner.utils.generateNotInstalledAppError
import com.google.zxing.*
import com.google.zxing.client.result.URIParsedResult
import com.google.zxing.common.HybridBinarizer
import java.util.*

class AndroidServicesInteractor(private val context: Context) {
    fun copyToClipBoard(textToCopy: String, activity: Activity) {
        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("RANDOM UUID", textToCopy)
        clipboard.setPrimaryClip(clip)
    }

    fun decodeWithZxing(bitmap: Bitmap): Result? {
        val multiFormatReader = MultiFormatReader()
        val hints: MutableMap<DecodeHintType, Any?> = Hashtable()
        hints[DecodeHintType.PURE_BARCODE] = java.lang.Boolean.TRUE
        multiFormatReader.setHints(hints)
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        var rawResult: Result? = null
        val source = RGBLuminanceSource(width, height, pixels)
        if (source != null) {
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            try {
                rawResult = multiFormatReader.decodeWithState(binaryBitmap)
            } catch (re: ReaderException) {
                re.printStackTrace()
            } finally {
                multiFormatReader.reset()
            }
        }
        return rawResult
    }
    fun addToContacts(contact: Contact) {
        val contactIntent = Intent(Intent.ACTION_INSERT).apply {
            type = ContactsContract.RawContacts.CONTENT_TYPE
            putExtra(Insert.PHONE, contact.number)
            putExtra(Insert.EMAIL, contact.email)
            putExtra(Insert.NAME, contact.name)
            putExtra(Insert.NOTES, contact.notes)
            putExtra(Insert.POSTAL, contact.postal)
            putExtra(Insert.COMPANY, contact.company)
            putExtra("finishActivityOnSaveCompleted", true)

        }
        context.startActivity(contactIntent)
    }

    fun callPhone(phone: String) {
        val intent =
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phone}"))
        context.startActivity(intent)
    }

    fun showOnMap(latitude: Double = 0.0, longitude: Double = 0.0, address: String = "") {
        val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$address")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        context.startActivity(mapIntent)
    }

    fun sendSms(text: String, number: String = "") {
        if (number.isNotEmpty()) {
            val uri = Uri.parse("smsto:${number}")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.putExtra("sms_body", text)
            context.startActivity(intent)
        }
        val sendIntent = Intent(Intent.ACTION_VIEW).apply {
            putExtra("sms_body", text)
            type = "vnd.android-dir/mms-sms"
        }
        context.startActivity(sendIntent)
    }

    fun sendEmail(text: String, subject: String = "", recipient: String = "") {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("mailto:")
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(sendIntent)
    }

    fun onBrowseClick(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(Intent.createChooser(intent, "Browse with"))
    }

    fun openInstagram(uri: URIParsedResult) {
        val likeIng = Intent(Intent.ACTION_VIEW, Uri.parse(uri.uri))
        likeIng.setPackage("com.instagram.android")
        try {
            context.startActivity(likeIng)
        } catch (e: ActivityNotFoundException) {
            try {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(uri.uri)
                    )
                )
            } catch (e: Exception) {
                generateNotInstalledAppError("Instagram", context)
            }
        }
    }

    fun openFacebook(uri: URIParsedResult) {
        val fbIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri.uri))
        try {
            context.startActivity(fbIntent)
        } catch (e: Exception) {
            generateNotInstalledAppError("Facebook", context)
        }
    }

    fun sendInWhatsapp(phone: String) {
        var number = phone
        if (!phone.first().isDigit()) number = phone.substring(1)
        val uri = Uri.parse("smsto:$number")
        val i = Intent(Intent.ACTION_SENDTO, uri)
        i.setPackage("com.whatsapp")
        try {
            context.startActivity(i)
        } catch (e: Exception) {
            generateNotInstalledAppError("WhatsApp", context)
        }
    }

    fun shareText(text: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    fun openURI(uri: URIParsedResult, codeType: CodeType) {
        when (codeType) {
            CodeType.INSTAGRAM -> openInstagram(uri)
            CodeType.FACEBOOK -> openFacebook(uri)
            CodeType.WHATSAPP -> sendInWhatsapp(uri.uri.takeLast(12))
            else -> onBrowseClick(uri.uri)
        }
    }

    fun enableWiFiConnection() {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.isWifiEnabled = true
        context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
    }
}