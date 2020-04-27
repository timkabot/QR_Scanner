package com.app.qrscanner.domain.interactors

import android.app.Activity
import android.app.Service
import android.content.*
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Vibrator
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.ContactsContract.Intents.Insert
import android.provider.Settings
import com.app.qrscanner.R
import com.app.qrscanner.domain.entities.CodeType
import com.app.qrscanner.domain.entities.Contact
import com.app.qrscanner.utils.generateNotInstalledAppError
import com.google.zxing.*
import com.google.zxing.client.result.CalendarParsedResult
import com.google.zxing.client.result.URIParsedResult
import com.google.zxing.common.HybridBinarizer


class AndroidServicesInteractor() {
    fun copyToClipBoard(textToCopy: String, activity: Activity) {
        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("RANDOM UUID", textToCopy)
        clipboard.setPrimaryClip(clip)
    }

    fun vibrate(activity: Activity) {
        val vibrator = activity.application?.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(100)
    }

    fun playSound(activity: Activity) {
        val player = MediaPlayer.create(activity, R.raw.beep)
        player.setOnCompletionListener { player -> player.release() }
        player.start()
    }

    fun decodeWithZxing(bitmap: Bitmap): Result? {
        val multiFormatReader = MultiFormatReader()
//        val hints: MutableMap<DecodeHintType, Any?> = Hashtable()
//        hints[DecodeHintType.PURE_BARCODE] = java.lang.Boolean.TRUE
       // multiFormatReader.setHints(hints)
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

            if (rawResult == null) {
                val invertedSource = source.invert()
                val binaryBitmap = BinaryBitmap(HybridBinarizer(invertedSource))
                try {
                    rawResult = multiFormatReader.decodeWithState(binaryBitmap)
                } catch (var27: NotFoundException) {
                } finally {
                    multiFormatReader.reset()
                }
            }
        }
        return rawResult
    }
    fun addCalendar(calendar: CalendarParsedResult, context: Context) {
        val beginTime = calendar.start
        val endTime = calendar.end
        val intent: Intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.time)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.time)
            .putExtra(CalendarContract.Events.TITLE, calendar.summary)
            .putExtra(CalendarContract.Events.DESCRIPTION, calendar.description)
            .putExtra(CalendarContract.Events.EVENT_LOCATION,calendar.location)
        context.startActivity(intent)
    }
    fun addToContacts(contact: Contact, context: Context) {
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

    fun callPhone(phone: String, context: Context) {
        val intent =
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phone}"))
        context.startActivity(intent)
    }

    fun showOnMap(latitude: Double = 0.0, longitude: Double = 0.0, address: String = "", context: Context) {
        val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$address")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        context.startActivity(mapIntent)
    }

    fun sendSms(text: String, number: String = "", context: Context) {
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

    fun sendEmail(text: String, subject: String = "", recipient: String = "", context: Context) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("mailto:")
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(sendIntent)
    }

    fun onBrowseClick(url: String, context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(Intent.createChooser(intent, "Browse with"))
    }

    fun openInstagram(uri: URIParsedResult, context: Context) {
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

    fun openFacebook(uri: URIParsedResult, context: Context) {
        val fbIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri.uri))
        try {
            context.startActivity(fbIntent)
        } catch (e: Exception) {
            generateNotInstalledAppError("Facebook", context)
        }
    }

    fun sendInWhatsapp(phone: String, context: Context) {
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

    fun shareText(text: String, context: Context) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    fun openURI(uri: URIParsedResult, codeType: CodeType, context: Context) {
        when (codeType) {
            CodeType.INSTAGRAM -> openInstagram(uri, context)
            CodeType.FACEBOOK -> openFacebook(uri, context)
            CodeType.WHATSAPP -> sendInWhatsapp(uri.uri.takeLast(12), context)
            else -> onBrowseClick(uri.uri, context)
        }
    }

    fun enableWiFiConnection(context: Context) {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.isWifiEnabled = true
        context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
    }
}