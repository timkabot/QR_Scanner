package com.app.qrscanner.presentation.createQr

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.app.qrscanner.R
import com.app.qrscanner.Screens
import com.app.qrscanner.domain.entities.Code
import com.app.qrscanner.domain.entities.CodeStatus
import com.app.qrscanner.domain.entities.CodeType
import com.app.qrscanner.domain.interactors.DatabaseInteractor
import com.app.qrscanner.presentation.ContainerActivity
import com.app.qrscanner.presentation.global.BaseFragment
import com.app.qrscanner.presentation.global.list.CodeTypesAdapter
import kotlinx.android.synthetic.main.fragment_create.*
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.Router

class CreateCodeFragment : BaseFragment() {
    private val router by inject<Router>()
    override val layoutRes = R.layout.fragment_create
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var codesTypesAdapter: CodeTypesAdapter
    private val databaseInteractor by inject<DatabaseInteractor>()

    private val activity
        get() = getActivity() as ContainerActivity

    private fun initListeners() {
        createCodeButton.setOnClickListener {
            editText.text.let {
                if (it.isNotEmpty()) {
                    databaseInteractor.saveCodeInDatabase(
                        Code(
                            data = it.toString(),
                            status = CodeStatus.CREATED
                        )
                    )
                    router.navigateTo(
                        Screens.ShowCreatedQRScreen(editText.text.toString())
                    )
                }
            }
        }

        pasteButton.setOnClickListener {
            val clipboard =
                activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData: ClipData? = clipboard.primaryClip
            clipData?.apply {
                val textToPaste: String = this.getItemAt(0).text.toString().trim()
                editText.setText(textToPaste)
            }
        }
    }

    private fun initRecycler() {
        codesTypesAdapter =
            CodeTypesAdapter(
                CodeType.values().toList()
                    .filter { (it != CodeType.URI && it != CodeType.VIN && it != CodeType.GEO) },
                object : onCodeCreateClickListener {
                    override fun onClick(codeType: CodeType) {
                        changeToolbar(false)
                        router.navigateTo(
                            when (codeType) {
                                CodeType.WIFI -> Screens.CreateWifiQrScreen
                                CodeType.FACEBOOK -> Screens.CreateFacebookQrScreen
                                CodeType.WHATSAPP -> Screens.CreateWhatsAppQrScreen
                                CodeType.SPOTIFY -> Screens.CreateSpotifyQrScreen
                                CodeType.YOUTUBE -> Screens.CreateYoutubeQrScreen
                                CodeType.PAYPAL -> Screens.CreatePaypalQrScreen
                                CodeType.CARD -> Screens.CreateCardQrScreen
                                CodeType.VIBER -> Screens.CreateViberQrScreen
                                CodeType.INSTAGRAM -> Screens.CreateInstagramQrScreen
                                CodeType.TWITTER -> Screens.CreateTwitterQrScreen
                                CodeType.TEXT -> Screens.CreateTextQrScreen
                                CodeType.CALENDAR -> Screens.CreateCalendarQrScreen
                                CodeType.TEL -> Screens.CreatePhoneQrScreen
                                CodeType.SMS -> Screens.CreateSmsQrScreen
                                CodeType.EMAIL_ADDRESS -> Screens.CreateMailQrScreen
                                CodeType.ADDRESSBOOK -> Screens.CreateContactQrScreen
                                else -> Screens.CreateInstagramQrScreen
                            }
                        )
                    }
                })
        gridLayoutManager = GridLayoutManager(context, 3)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = codesTypesAdapter
    }

    override fun onResume() {
        changeToolbar(true)
        super.onResume()
    }

    private fun changeToolbar(entered: Boolean) {
        if (entered) {
            with(ContainerActivity) {
                setAppBatTitle("", activity)
                changeSettingButtonVisibility(activity, View.VISIBLE)
                changeAdsButtonVisibility(activity, View.VISIBLE)
                changeNoAdsButtonVisibility(activity, View.GONE)
                changeBackButtonVisibility(activity, View.VISIBLE)
                changeCreateQrButtonVisibility(activity, View.GONE)
            }
        } else {
            with(ContainerActivity) {
                setAppBatTitle("", activity)
                changeSettingButtonVisibility(activity, View.GONE)
                changeAdsButtonVisibility(activity, View.GONE)
                changeNoAdsButtonVisibility(activity, View.GONE)
                changeBackButtonVisibility(activity, View.VISIBLE)
                changeCreateQrButtonVisibility(activity, View.VISIBLE)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initListeners()
        initRecycler()
    }
}

interface onCodeCreateClickListener {
    fun onClick(codeType: CodeType)
}