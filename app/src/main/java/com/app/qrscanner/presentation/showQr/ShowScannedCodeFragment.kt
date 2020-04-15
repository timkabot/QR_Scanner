package com.app.qrscanner.presentation.showQr

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.view.View
import com.app.qrscanner.R
import com.app.qrscanner.data.CodesRepository
import com.app.qrscanner.domain.entities.Code
import com.app.qrscanner.domain.entities.SerializableResult
import com.app.qrscanner.presentation.global.BaseFragment
import com.app.qrscanner.utils.*
import com.google.zxing.client.result.*
import com.google.zxing.client.result.ParsedResultType.*
import kotlinx.android.synthetic.main.fragment_show_scanned_code.*
import org.koin.android.ext.android.inject


class ShowScannedCodeFragment : BaseFragment() {
    override val layoutRes = R.layout.fragment_show_scanned_code
    private val qrCodeValue by argument(QR_CODE_VALUE, "")
    private val result by argument<SerializableResult>(RESULT_VALUE, null)
    private val codesRepository by inject<CodesRepository>()


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        result.let { serializableResult ->
            serializableResult.result?.let {
                val parsedResult = ResultParser.parseResult(it)
                initContentTextView(parsedResult)
                codeTypeTextView.text = "${parsedResult.type}"

                val codeType = getCodeTypeForParsedResult(parsedResult)
                codeTypeImage.setImageResource(getImageForCodeType(codeType))
            }
        }
    }

    private fun setContent(text: String) {
        codeContentTextView.text = text
    }

    private fun initContentTextView(parsedResult: ParsedResult) {
        when (parsedResult.type) {
            ADDRESSBOOK -> initAddressBook(parsedResult as AddressBookParsedResult)
            WIFI -> initWifi(parsedResult as WifiParsedResult)
            EMAIL_ADDRESS -> initEmail(parsedResult as EmailAddressParsedResult)
            URI -> initUrl(parsedResult as URIParsedResult)
            TEXT -> initText(parsedResult as TextParsedResult)
            GEO -> initGeo(parsedResult as GeoParsedResult)
            TEL -> initTel(parsedResult as TelParsedResult)
            SMS -> initSms(parsedResult as SMSParsedResult)
            CALENDAR -> initCalendar(parsedResult as CalendarParsedResult)
            ISBN -> initISBN(parsedResult as ISBNParsedResult)
        }

    }

    private fun initISBN(isbn: ISBNParsedResult) {
        setContent(isbn.isbn)
        isbnLayout.visibility = View.VISIBLE
        isbnSearch.setOnClickListener {
            val searchIntent = Intent(Intent.ACTION_SEARCH).apply {
                putExtra("QUERY", isbn.isbn)
            }
            startActivity(searchIntent)
        }

        isbnShare.setOnClickListener {
            shareText(isbn.isbn)
        }
    }

    private fun saveCodeInDatabase(code: Code) {
        Thread {
            codesRepository.insertCode(code)
        }.also {
            it.start()
        }
    }

    private fun initCalendar(calendar: CalendarParsedResult) {
        var info = ""

        whenNotNull(calendar.attendees) { info += "Участники: ${calendar.attendees.contentToString()}\n" }
        whenNotNull(calendar.description) { info += "Описание: ${calendar.description}\n" }
        whenNotNull(calendar.organizer) { info += "Организатор ${calendar.organizer}\n" }
        whenNotNull(calendar.start) {
            info += "Начало: ${calendar.start}\n"
        }
        whenNotNull(calendar.end) { info += "Конец: ${calendar.end}\n" }
        whenNotNull(calendar.location) { info += "Местоположение: ${calendar.location}" }

        setContent(info)
        calendarLayout.visibility = View.VISIBLE
    }

    private fun initSms(sms: SMSParsedResult) {
        var info = ""
        whenNotNull(sms.numbers) { info += "Номер: ${sms.numbers?.contentToString()}\n" }
        whenNotNull(sms.subject) { info += "Тема сообщения: ${sms.subject}\n" }
        whenNotNull(sms.body) { info += "Текст сообщения: ${sms.body} " }
        setContent(info)
        smsLayout.visibility = View.VISIBLE

        smsSendButton.setOnClickListener {
            sendSms(number = sms.numbers[0], text = sms.body)
        }
        smsCallButton.setOnClickListener {
            onCallClicked(sms.numbers[0])
        }
    }

    private fun initTel(tel: TelParsedResult) {
        var info = ""
        whenNotNull(tel.title) { info += "Заголовок ${tel.title}" }
        whenNotNull(tel.number) { info += "Номер ${tel.number}" }
        setContent(info)
        telLayout.visibility = View.VISIBLE

        telCallButton.setOnClickListener { onCallClicked(tel.number) }
        telAddToContactsButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                type = ContactsContract.RawContacts.CONTENT_TYPE
                putExtra(
                    ContactsContract.Intents.Insert.PHONE, tel.number
                )
                putExtra("finishActivityOnSaveCompleted", true)

            }
            startActivity(intent)
        }
    }

    private fun initGeo(geo: GeoParsedResult) {
        var info = ""
        whenNotNull(geo.altitude) { info += "Высота над уровнем моря: ${geo.altitude}\n" }
        whenNotNull(geo.longitude) { info += "Долгота: ${geo.longitude}\n" }
        whenNotNull(geo.latitude) { info += "Широта: ${geo.latitude}\n" }
        setContent(info)
        geoLayout.visibility = View.VISIBLE
        geoShareButton.setOnClickListener { shareText(info) }
        geoShowButton.setOnClickListener { showOnMap(geo.latitude, geo.longitude) }
    }

    private fun sendSms(text: String, number: String = "") {
        if (number.isNotEmpty()) {
            val uri = Uri.parse("smsto:${number}")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.putExtra("sms_body", text)
            startActivity(intent)
        }
        val sendIntent = Intent(Intent.ACTION_VIEW).apply {
            putExtra("sms_body", text)
            type = "vnd.android-dir/mms-sms"
        }
        startActivity(sendIntent)

    }

    private fun sendEmail(text: String, subject: String = "", recipient: String = "") {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("mailto:")
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(sendIntent)

    }

    private fun initText(text: TextParsedResult) {
        var info = ""
        whenNotNull(text.language) { info += "Язык: ${text.language}\n" }
        info += text.text
        setContent(info)
        textLayout.visibility = View.VISIBLE
        textSendEmail.setOnClickListener { sendEmail(info) }
        textSendSms.setOnClickListener { sendSms(info) }
    }

    private fun onBrowseClick(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(Intent.createChooser(intent, "Browse with"))
    }

    private fun initUrl(url: URIParsedResult) {
        var info = "${url.uri}\n"
        codeContentTextView.maxLines = 1

        whenNotNull(url.title) { info += "Заголовок: ${url.title}" }
        setContent(info)
        urlLayout.visibility = View.VISIBLE
        urlShareButton.setOnClickListener {
            shareText(info)
        }
        println(url.uri)
        urlOpenInBrowserButton.setOnClickListener { onBrowseClick(url.uri) }
    }

    private fun initEmail(email: EmailAddressParsedResult) {
        val info = getEmailInfo(email)
        setContent(info)
        emailLayout.visibility = View.VISIBLE
        emailAddToContactsButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                type = ContactsContract.RawContacts.CONTENT_TYPE
                putExtra(
                    ContactsContract.Intents.Insert.EMAIL,
                    email.tos?.get(0)
                )
                putExtra("finishActivityOnSaveCompleted", true)
            }
            startActivity(intent)
        }
    }

    private fun initAddressBook(addressBook: AddressBookParsedResult) {
        val info = getAddressBookInfo(addressBook)
        setContent(info)
        addressBookLayout.visibility = View.VISIBLE

        addressBookAddToContactsButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                type = ContactsContract.RawContacts.CONTENT_TYPE
                putExtra(
                    ContactsContract.Intents.Insert.EMAIL,
                    addressBook.emails?.get(0)
                )
                putExtra(
                    ContactsContract.Intents.Insert.NAME,
                    addressBook.names?.get(0)
                )
                putExtra(
                    ContactsContract.Intents.Insert.PHONE, addressBook.phoneNumbers?.get(0)
                )
                putExtra(
                    ContactsContract.Intents.Insert.NOTES,
                    addressBook.note
                )
                putExtra(
                    ContactsContract.Intents.Insert.POSTAL,
                    addressBook.addresses?.get(0)
                )
                putExtra(
                    ContactsContract.Intents.Insert.COMPANY,
                    addressBook.addresses?.get(0)
                )
                putExtra("finishActivityOnSaveCompleted", true)

            }
            startActivity(intent)
        }
        addressBookCallContactButton.setOnClickListener {
            addressBook.phoneNumbers?.get(0)?.let { it1 -> onCallClicked(it1) }
        }
        addressShowOnMapButton.setOnClickListener {
            addressBook.addresses?.get(0)?.let { it1 -> showOnMap(address = it1) }
        }
    }

    private fun onCallClicked(phone: String) {
        val intent =
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phone}"))
        startActivity(intent)
    }

    private fun showOnMap(latitude: Double = 0.0, longitude: Double = 0.0, address: String = "") {
        val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$address")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        startActivity(mapIntent)
    }

    private fun enableWiFiConnection() {
        val wifiManager =
            activity?.application?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.isWifiEnabled = true
        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
    }

    private fun initWifi(wifi: WifiParsedResult) {
        val info =
            "Имя сети: ${wifi.ssid} \nПароль: ${wifi.password} \nТип шифрования: ${wifi.networkEncryption}"
        setContent(info)
        wifiLayout.visibility = View.VISIBLE
        wifiConnectButton.setOnClickListener {
            enableWiFiConnection()
        }
        wifiShareButton.setOnClickListener {
            shareText(info)
        }
    }

    private fun shareText(text: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    companion object {
        private const val QR_CODE_VALUE = "qr_code_value"
        private const val RESULT_VALUE = "result_value"

        fun create(qrCodeValue: String, result: SerializableResult) =
            ShowScannedCodeFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(QR_CODE_VALUE, qrCodeValue)
                    putSerializable(RESULT_VALUE, result)
                }
            }
    }
}