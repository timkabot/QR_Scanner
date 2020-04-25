package com.app.qrscanner.presentation.showQr

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.qrscanner.R
import com.app.qrscanner.domain.entities.CodeType
import com.app.qrscanner.domain.interactors.MyResultParser
import com.app.qrscanner.presentation.ContainerActivity
import com.app.qrscanner.presentation.MainViewModel
import com.app.qrscanner.presentation.global.BaseFragment
import com.app.qrscanner.utils.argument
import com.app.qrscanner.utils.createQR
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.google.zxing.client.result.ResultParser
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_show_created_code.*
import org.koin.android.ext.android.inject

class ShowCreatedCodeFragment : BaseFragment() {
    override val layoutRes = R.layout.fragment_show_created_code
    private val qrCodeValue by argument(QR_CODE_VALUE, "")
    private val qrCodeType by argument<CodeType>(QR_CODE_TYPE, null)

    private val textParser by inject<MyResultParser>()
    private val mainVM by inject<MainViewModel>()
    private val activity
        get() = getActivity() as ContainerActivity
    private lateinit var qrBitmap: Bitmap
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        qrBitmap = createQR(qrCodeValue)
        qrCodeImageView.setImageBitmap(qrBitmap)
        val result = mainVM.androidServicesInteractor.decodeWithZxing(qrBitmap)
        textParser.result = ResultParser.parseResult(result)
        println("Parsed result \n${textParser.result.displayResult}\n Type:\n${textParser.result.type}")

        if(qrCodeType!= null ){
            icon.setImageResource(mainVM.codeTypeInteractor.getImageForCodeType(qrCodeType))
        }

        textView.text = textParser.appropriateTextFromQrValue(qrCodeValue)

        initListeners()
        initNativeAds()
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

    override fun onResume() {
        super.onResume()
        changeToolbar(true)
        activity.findViewById<FrameLayout>(R.id.adViewContainer).visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        activity.findViewById<FrameLayout>(R.id.adViewContainer).visibility = View.VISIBLE

    }

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


    private fun initListeners() {
        saveButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!isWriteToStoragePermissionGranted()) {
                    ActivityCompat.requestPermissions(
                        activity as Activity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        123
                    )
                    return@setOnClickListener
                }
            }
            showDialog()
        }
        shareButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!isWriteToStoragePermissionGranted()) {
                    ActivityCompat.requestPermissions(
                        activity as Activity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        123
                    )
                    return@setOnClickListener
                }
            }
            shareImage(qrBitmap)
        }

    }

    private fun showDialog() {
        context?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = layoutInflater
            builder.setTitle(getString(R.string.save_qr_code))
            val dialogLayout = inflater.inflate(R.layout.dialog_save_code, null)
            val editText = dialogLayout.findViewById<EditText>(R.id.editText)
            builder.setView(dialogLayout)
            builder.setPositiveButton("Сохранить") { _, _ ->
                saveImage(qrBitmap, editText.text.toString())
                Toast.makeText(
                    context,
                    getString(R.string.saved_to_gallery) + editText.text.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
            builder.show()
        }
    }

    // Method to save an image to gallery and return uri
    private fun saveImage(bitmap: Bitmap, title: String): Uri {
        val savedImageURL = MediaStore.Images.Media.insertImage(
            activity.contentResolver,
            bitmap,
            title,
            "Image of $title"
        )
        return Uri.parse(savedImageURL)
    }

    private fun shareImage(bitmap: Bitmap) {
        val uri = saveImage(bitmap, "qrForSaving")
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(intent, "Share image via"))
    }

    private fun isWriteToStoragePermissionGranted(): Boolean {
        val selfPermission =
            ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return selfPermission == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val QR_CODE_VALUE = "qr_code_value"
        private const val QR_CODE_TYPE = "qr_code_type"

        fun create(qrCodeValue: String, codeType: CodeType?) =
            ShowCreatedCodeFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(QR_CODE_VALUE, qrCodeValue)
                    putSerializable(QR_CODE_TYPE, codeType)
                }
            }
    }
}