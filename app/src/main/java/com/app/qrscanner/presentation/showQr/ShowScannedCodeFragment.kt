package com.app.qrscanner.presentation.showQr

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.app.qrscanner.R
import com.app.qrscanner.domain.entities.CodeType
import com.app.qrscanner.domain.entities.Contact
import com.app.qrscanner.domain.entities.SerializableResult
import com.app.qrscanner.domain.interactors.AndroidServicesInteractor
import com.app.qrscanner.domain.interactors.CodeTypeInteractor
import com.app.qrscanner.domain.interactors.ParsedResultInteractor
import com.app.qrscanner.presentation.global.BaseFragment
import com.app.qrscanner.utils.argument
import com.app.qrscanner.utils.whenNotNull
import com.google.zxing.client.result.*
import com.google.zxing.client.result.ParsedResultType.*
import kotlinx.android.synthetic.main.fragment_show_scanned_code.*
import org.koin.android.ext.android.inject


class ShowScannedCodeFragment : BaseFragment() {
    override val layoutRes = R.layout.fragment_show_scanned_code
    private val result by argument<SerializableResult>(RESULT_VALUE, null)

    private val parsedResultInteractor by inject<ParsedResultInteractor>()
    private val codeTypeInteractor by inject<CodeTypeInteractor>()
    private val androidServicesInteractor by inject<AndroidServicesInteractor>()


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        result.let { serializableResult ->
            serializableResult.result?.let {
                val parsedResult = ResultParser.parseResult(it)
                initContentTextView(parsedResult)
                codeTypeTextView.text = "${parsedResult.type}"

                val codeType = parsedResultInteractor.getCodeTypeForParsedResult(parsedResult)
                codeTypeImage.setImageResource(codeTypeInteractor.getImageForCodeType(codeType))
            }
        }
        retainInstance = true

    }

    private fun setContent(text: String) {
        codeContentTextView.text = text
    }

    private fun initContentTextView(parsedResult: ParsedResult) {
        when (parsedResult.type) {
            ADDRESSBOOK -> initAddressBook(parsedResult as AddressBookParsedResult)
            WIFI -> initWifi(parsedResult as WifiParsedResult)
            EMAIL_ADDRESS -> initEmail(parsedResult as EmailAddressParsedResult)
            URI -> initUri(
                parsedResult as URIParsedResult,
                parsedResultInteractor.getCodeTypeForParsedResult(parsedResult)
            )
            TEXT -> initText(parsedResult as TextParsedResult)
            GEO -> initGeo(parsedResult as GeoParsedResult)
            TEL -> initTel(parsedResult as TelParsedResult)
            SMS -> initSms(parsedResult as SMSParsedResult)
            CALENDAR -> initCalendar(parsedResult as CalendarParsedResult)
            ISBN -> initISBN(parsedResult as ISBNParsedResult)
            PRODUCT -> initProduct(parsedResult as ProductParsedResult)
            VIN -> initVin(parsedResult as VINParsedResult)
        }
    }

    private fun initVin(vin : VINParsedResult){
        //TODO
    }

    private fun initProduct(product: ProductParsedResult){
        //todo
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
            androidServicesInteractor.shareText(isbn.isbn)
        }
    }

    private fun initCalendar(calendar: CalendarParsedResult) {
        val info = parsedResultInteractor.getInfoForCalendar(calendar)
        setContent(info)
        calendarLayout.visibility = View.VISIBLE
    }

    private fun initSms(sms: SMSParsedResult) {
        val info = parsedResultInteractor.getInfoForSms(sms)
        setContent(info)
        smsLayout.visibility = View.VISIBLE


        smsSendButton.setOnClickListener {
            androidServicesInteractor.sendSms(number = sms.numbers[0], text = sms.body)
        }
        smsCallButton.setOnClickListener {
            androidServicesInteractor.callPhone(sms.numbers[0])
        }
    }

    private fun initTel(tel: TelParsedResult) {
        val info = parsedResultInteractor.getInfoForTel(tel)
        setContent(info)

        telLayout.visibility = View.VISIBLE

        telCallButton.setOnClickListener { androidServicesInteractor.callPhone(tel.number) }
        telAddToContactsButton.setOnClickListener {
            whenNotNull(tel.number) { androidServicesInteractor.addToContacts(Contact(number = tel.number)) }
        }
    }

    private fun initGeo(geo: GeoParsedResult) {
        val info = parsedResultInteractor.getInfoForGeo(geo)
        setContent(info)
        geoLayout.visibility = View.VISIBLE
        geoShareButton.setOnClickListener { androidServicesInteractor.shareText(info) }
        geoShowButton.setOnClickListener {
            androidServicesInteractor.showOnMap(geo.latitude, geo.longitude)
        }
    }

    private fun initText(text: TextParsedResult) {
        val info = parsedResultInteractor.getInfoForText(text)
        setContent(info)
        textLayout.visibility = View.VISIBLE

        textSendEmail.setOnClickListener { androidServicesInteractor.sendEmail(info) }
        textSendSms.setOnClickListener { androidServicesInteractor.sendSms(info) }
    }


    private fun initUri(uri: URIParsedResult, codeType: CodeType) {

        val info = parsedResultInteractor.getInfoForURI(uri)
        codeContentTextView.maxLines = 1

        setContent(info)
        urlLayout.visibility = View.VISIBLE
        urlShareButton.setOnClickListener { androidServicesInteractor.shareText(info) }
        urlOpenInBrowserButton.setOnClickListener {
            androidServicesInteractor.openURI(
                uri,
                codeType
            )
        }
    }


    private fun initEmail(email: EmailAddressParsedResult) {
        val info = parsedResultInteractor.getInfoForEmail(email)
        setContent(info)
        emailLayout.visibility = View.VISIBLE


        emailAddToContactsButton.setOnClickListener {
            val contact = Contact(email = email.tos?.get(0))
            androidServicesInteractor.addToContacts(contact)
        }
    }


    private fun initAddressBook(addressBook: AddressBookParsedResult) {
        val info = parsedResultInteractor.getInfoForAddressBook(addressBook)
        setContent(info)
        addressBookLayout.visibility = View.VISIBLE

        addressBookAddToContactsButton.setOnClickListener {
            val contact = Contact(
                number = addressBook.phoneNumbers?.get(0),
                email = addressBook.emails?.get(0),
                name = addressBook.names?.get(0),
                notes = addressBook.note,
                postal = addressBook.addresses?.get(0),
                company = addressBook.addresses?.get(0)
            )
            androidServicesInteractor.addToContacts(contact)
        }

        addressBookCallContactButton.setOnClickListener {
            addressBook.phoneNumbers?.get(0)
                ?.let { it1 -> androidServicesInteractor.callPhone(it1) }
        }
        addressShowOnMapButton.setOnClickListener {
            addressBook.addresses?.get(0)
                ?.let { it1 -> androidServicesInteractor.showOnMap(address = it1) }
        }
    }


    private fun initWifi(wifi: WifiParsedResult) {
        val info = parsedResultInteractor.getInfoForWifi(wifi)
        setContent(info)
        wifiLayout.visibility = View.VISIBLE


        wifiConnectButton.setOnClickListener {
            androidServicesInteractor.enableWiFiConnection()
        }
        wifiShareButton.setOnClickListener {
            androidServicesInteractor.shareText(info)
        }
    }


    companion object {
        private const val RESULT_VALUE = "result_value"

        fun create(result: SerializableResult) =
            ShowScannedCodeFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(RESULT_VALUE, result)
                }
            }
    }
}