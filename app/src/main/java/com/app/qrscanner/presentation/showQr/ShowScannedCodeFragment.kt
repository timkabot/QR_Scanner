package com.app.qrscanner.presentation.showQr

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.app.qrscanner.R
import com.app.qrscanner.domain.entities.CodeType
import com.app.qrscanner.domain.entities.Contact
import com.app.qrscanner.domain.entities.SerializableResult
import com.app.qrscanner.domain.interactors.CodeTypeInteractor
import com.app.qrscanner.presentation.ContainerActivity
import com.app.qrscanner.presentation.MainViewModel
import com.app.qrscanner.presentation.global.BaseFragment
import com.app.qrscanner.utils.showToast
import com.app.qrscanner.utils.whenNotNull
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.google.zxing.client.result.*
import com.google.zxing.client.result.ParsedResultType.*
import kotlinx.android.synthetic.main.fragment_show_scanned_code.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel


class ShowScannedCodeFragment : BaseFragment() {
    override val layoutRes = R.layout.fragment_show_scanned_code

    private val codeTypeInteractor by inject<CodeTypeInteractor>()
    private val mainVM: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    private val activity
        get() = getActivity() as ContainerActivity

    private fun initNativeAds() {
        val adLoader = AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
            .forUnifiedNativeAd { ad: UnifiedNativeAd ->

                val inflater =
                    context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val adView = inflater.inflate(R.layout.native_ad, null) as UnifiedNativeAdView

                val icon = adView.findViewById<ImageView>(R.id.adAppIcon)
                icon.setImageDrawable(ad.icon.drawable)
                adView.iconView = icon

                val headline = adView.findViewById<TextView>(R.id.adHeadline)
                headline.text = ad.headline
                adView.headlineView = headline

                val description = adView.findViewById<TextView>(R.id.adDescription)
                description.text = ad.body
                adView.bodyView = description

                val button = adView.findViewById<Button>(R.id.adButton)
                button.text = ad.callToAction
                adView.callToActionView = button

                adView.setNativeAd(ad)
                adFrame.removeAllViews()
                adFrame.addView(adView)
            }
            .build()
        adLoader.loadAd(AdRequest.Builder().build())

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initNativeAds()
            mainVM.lastScannedResult?.let {
                val parsedResult = ResultParser.parseResult(it)
                initContentTextView(parsedResult)
                codeTypeTextView.text = codeTypeInteractor.getNameForCodeType( mainVM.parsedResultInteractor.getCodeTypeForParsedResult(parsedResult), context!!)

                val codeType = mainVM.parsedResultInteractor.getCodeTypeForParsedResult(parsedResult)
                codeTypeImage.setImageResource(codeTypeInteractor.getImageForCodeType(codeType))
        }

        copyButton.setOnClickListener {
            mainVM.androidServicesInteractor.copyToClipBoard(codeContentTextView.text.toString(), activity as Activity)
            getString(R.string.succesfully_copied_to_clipboard).showToast(context!!)
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
            URI -> initUri(
                parsedResult as URIParsedResult,
                mainVM.parsedResultInteractor.getCodeTypeForParsedResult(parsedResult)
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
            mainVM.androidServicesInteractor.shareText(isbn.isbn, context!!)
        }
    }

    private fun initCalendar(calendar: CalendarParsedResult) {
        val info = mainVM.parsedResultInteractor.getInfoForCalendar(calendar)
        setContent(info)
        calendarLayout.visibility = View.VISIBLE

        calendarAddEventButton.setOnClickListener {
            mainVM.androidServicesInteractor.addCalendar(calendar, context!!)
        }
        calendarSendEmail.setOnClickListener {
            mainVM.androidServicesInteractor.sendEmail(text = info,context =  context!!)
        }
    }

    private fun initSms(sms: SMSParsedResult) {
        val info = mainVM.parsedResultInteractor.getInfoForSms(sms)
        setContent(info)
        smsLayout.visibility = View.VISIBLE


        smsSendButton.setOnClickListener {
            mainVM.androidServicesInteractor.sendSms(number = sms.numbers[0], text = sms.body,context =  context!!)
        }
        smsCallButton.setOnClickListener {
            mainVM.androidServicesInteractor.callPhone(sms.numbers[0], context!!)
        }
    }

    private fun initTel(tel: TelParsedResult) {
        val info = mainVM.parsedResultInteractor.getInfoForTel(tel)
        setContent(info)

        telLayout.visibility = View.VISIBLE

        telCallButton.setOnClickListener { mainVM.androidServicesInteractor.callPhone(tel.number, context!!) }
        telAddToContactsButton.setOnClickListener {
            whenNotNull(tel.number) { mainVM.androidServicesInteractor.addToContacts(Contact(number = tel.number), context!!) }
        }
    }

    private fun initGeo(geo: GeoParsedResult) {
        val info = mainVM.parsedResultInteractor.getInfoForGeo(geo)
        setContent(info)
        geoLayout.visibility = View.VISIBLE
        geoShareButton.setOnClickListener { mainVM.androidServicesInteractor.shareText(info, context!!) }
        geoShowButton.setOnClickListener {
            mainVM.androidServicesInteractor.showOnMap(geo.latitude, geo.longitude, context = context!!)
        }
    }

    private fun initText(text: TextParsedResult) {
        val info = mainVM.parsedResultInteractor.getInfoForText(text)
        setContent(info)
        textLayout.visibility = View.VISIBLE

        textSendEmail.setOnClickListener { mainVM.androidServicesInteractor.sendEmail(info, context = context!!) }
        textSendSms.setOnClickListener { mainVM.androidServicesInteractor.sendSms(info, context = context!!) }
    }


    private fun initUri(uri: URIParsedResult, codeType: CodeType) {

        val info = mainVM.parsedResultInteractor.getInfoForURI(uri)
        codeContentTextView.maxLines = 1

        setContent(info)
        urlLayout.visibility = View.VISIBLE
        urlShareButton.setOnClickListener { mainVM.androidServicesInteractor.shareText(info, context!!) }
        urlOpenInBrowserButton.setOnClickListener {
            mainVM.androidServicesInteractor.openURI(
                uri,
                codeType, context!!
            )
        }
    }


    private fun initEmail(email: EmailAddressParsedResult) {
        val info = mainVM.parsedResultInteractor.getInfoForEmail(email)
        setContent(info)
        emailLayout.visibility = View.VISIBLE


        emailAddToContactsButton.setOnClickListener {
            val contact = Contact(email = email.tos?.get(0))
            mainVM.androidServicesInteractor.addToContacts(contact, context!!)
        }

        emailSendEmailButton.setOnClickListener {
            var recipient = email.tos?.get(0)
            if(recipient == null) recipient = ""
            mainVM.androidServicesInteractor.sendEmail(recipient = recipient, subject = email.subject, text = email.body, context = context!!)
        }

        emailShareButton.setOnClickListener {
            mainVM.androidServicesInteractor.shareText(info, context!!)
        }
    }


    private fun initAddressBook(addressBook: AddressBookParsedResult) {
        val info = mainVM.parsedResultInteractor.getInfoForAddressBook(addressBook)
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
            mainVM.androidServicesInteractor.addToContacts(contact, context!!)
        }

        addressBookCallContactButton.setOnClickListener {
            addressBook.phoneNumbers?.get(0)
                ?.let { it1 -> mainVM.androidServicesInteractor.callPhone(it1, context!!) }
        }
        addressShowOnMapButton.setOnClickListener {
            addressBook.addresses?.get(0)
                ?.let { it1 -> mainVM.androidServicesInteractor.showOnMap(address = it1, context = context!!) }
        }
    }


    private fun initWifi(wifi: WifiParsedResult) {
        val info = mainVM.parsedResultInteractor.getInfoForWifi(wifi)
        setContent(info)
        wifiLayout.visibility = View.VISIBLE


        wifiConnectButton.setOnClickListener {
            mainVM.androidServicesInteractor.enableWiFiConnection(context!!)
        }
        wifiShareButton.setOnClickListener {
            mainVM.androidServicesInteractor.shareText(info, context!!)
        }
    }

    private fun changeToolbar() {
        with(ContainerActivity) {
            setAppBatTitle("", activity)
            changeSettingButtonVisibility(activity, View.VISIBLE)
            changeAdsButtonVisibility(activity, View.VISIBLE)
            changeNoAdsButtonVisibility(activity, View.GONE)
            changeBackButtonVisibility(activity, View.VISIBLE)
            changeCreateQrButtonVisibility(activity, View.GONE)
        }
    }

    override fun onResume() {
        super.onResume()
        changeToolbar()
    }

    companion object {
        private const val RESULT_VALUE = "result_value"

        fun create(result: SerializableResult) =
            ShowScannedCodeFragment().apply {
                println("Trying to create fragment ${result}")
                arguments = Bundle().apply {
                    putSerializable(RESULT_VALUE, result)
                }
            }
    }
}