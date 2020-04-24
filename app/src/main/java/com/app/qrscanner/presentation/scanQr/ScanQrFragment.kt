package com.app.qrscanner.presentation.scanQr

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.marginBottom
import com.app.qrscanner.R
import com.app.qrscanner.Screens
import com.app.qrscanner.data.local.SettingsLocal
import com.app.qrscanner.domain.entities.Code
import com.app.qrscanner.domain.entities.CodeStatus
import com.app.qrscanner.domain.entities.SerializableResult
import com.app.qrscanner.domain.interactors.AndroidServicesInteractor
import com.app.qrscanner.domain.interactors.DatabaseInteractor
import com.app.qrscanner.domain.interactors.ParsedResultInteractor
import com.app.qrscanner.presentation.ContainerActivity
import com.app.qrscanner.presentation.MainViewModel
import com.app.qrscanner.presentation.customScannerView.MyZxing
import com.app.qrscanner.presentation.global.BaseFragment
import com.app.qrscanner.utils.HUAWEI
import com.app.qrscanner.utils.MY_CAMERA_REQUEST_CODE
import com.google.zxing.*
import com.google.zxing.client.result.ResultParser
import com.google.zxing.common.HybridBinarizer
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_scan.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import ru.terrakok.cicerone.Router
import java.util.*


class ScanQrFragment : BaseFragment(), MyZxing.ResultHandler {
    override val layoutRes = R.layout.fragment_scan
    private val databaseInteractor by inject<DatabaseInteractor>()
    private val router by inject<Router>()
    private val parsedResultInteractor by inject<ParsedResultInteractor>()
    private val androidServicesInteractor by inject<AndroidServicesInteractor>()
    private val PICK_IMAGE = 100
    private val mainViewModel: MainViewModel by viewModel()

    private val activity
        get() = getActivity() as ContainerActivity

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setScannerProperties()
        initListeners()
    }

    private fun initListeners() {
        val showFlashDrawable =
            AppCompatResources.getDrawable(context!!, R.drawable.flashlight_icon)
        val hideFlashDrawable =
            AppCompatResources.getDrawable(context!!, R.drawable.no_flashlight_icon)

        btnFlash.setOnClickListener {
            if (btnFlash.background == showFlashDrawable) {
                qrCodeScanner.flash = true
                btnFlash.background = hideFlashDrawable
            } else {
                qrCodeScanner.flash = false
                btnFlash.background = showFlashDrawable
            }
        }

        galleyPick.setOnClickListener {
            openGallery()
        }
    }


    private fun changeToolbar() {
        with(ContainerActivity) {
            setAppBatTitle("", activity)
            changeSettingButtonVisibility(activity, View.VISIBLE)
            changeAdsButtonVisibility(activity, View.VISIBLE)
            changeNoAdsButtonVisibility(activity, View.GONE)
            changeBackButtonVisibility(activity, View.GONE)
            changeCreateQrButtonVisibility(activity, View.GONE)
        }
    }

    private fun setScannerProperties() {
        qrCodeScanner.setAutoFocus(true)
        qrCodeScanner.setBorderLineLength(250)
        qrCodeScanner.setBorderStrokeWidth(10)
        qrCodeScanner.setMaskColor(getColor(context!!,R.color.semi_transparent))
        qrCodeScanner.setSquareViewFinder(false)


        //= qrCodeScanner.viewFinder.mFramingRect!!.bottom
        if (Build.MANUFACTURER.equals(HUAWEI, ignoreCase = true))
            qrCodeScanner.setAspectTolerance(0.5f)
        qrCodeScanner.startCamera()

    }
    private fun openGallery() {
//        val gallery =
//            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//        startActivityForResult(gallery, PICK_IMAGE)
        CropImage.activity()
            .start(context!!, this);
    }
    private fun playSound() {
        val player = MediaPlayer.create(activity, R.raw.beep)
        player.setOnCompletionListener { player -> player.release() }
        player.start()
    }

    override fun onResume() {
        super.onResume()
        changeToolbar()
        println(qrCodeScanner.viewFinder.framingRect!!)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context?.let {
                if (!isCameraPermissionGranted()) {
                    ActivityCompat.requestPermissions(
                        activity as Activity, arrayOf(Manifest.permission.CAMERA),
                        MY_CAMERA_REQUEST_CODE
                    )
                    return
                }
            }
        }

        qrCodeScanner.resumeCameraPreview(this)
    }

    private fun isCameraPermissionGranted(): Boolean {
        val selfPermission = checkSelfPermission(context!!, Manifest.permission.CAMERA)
        return selfPermission == PackageManager.PERMISSION_GRANTED
    }

    override fun onPause() {
        super.onPause()
        qrCodeScanner.stopCameraPreview()
    }


    private fun vibrate() {
        val vibrator = activity.application?.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(100)
    }


    override fun handleResult(p0: Result?) {
        println("Scanned ${p0!!.text}")
        p0.let {
            if (SettingsLocal.vibrate) vibrate()
            if (SettingsLocal.autoCopy) androidServicesInteractor.copyToClipBoard(p0.text, activity)
            if (SettingsLocal.beep) playSound()

            println()
            saveCodeToDatabase(p0)
            mainViewModel.lastScannedResult = p0
            router.navigateTo(Screens.ShowScannedQRScreen)
        }
    }

    private fun saveCodeToDatabase(res: Result) {
        val parsedResult = ResultParser.parseResult(res)
        val code = Code(data = res.text, status = CodeStatus.SCANNED).apply {
            type = parsedResultInteractor.getCodeTypeForParsedResult(parsedResult)
            shortDescription = parsedResultInteractor.getInfoForParsedResult(parsedResult)
            result = res
        }
        if (code.shortDescription.isEmpty())
            code.shortDescription = res.text
        databaseInteractor.saveCodeInDatabase(code)
    }

    private fun openCamera() {
        qrCodeScanner.startCamera()
        qrCodeScanner.setResultHandler(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            val resultUri = CropImage.getActivityResult(data).uri
            println("get cropped $resultUri")
            val bitmap =
                MediaStore.Images.Media.getBitmap(activity.contentResolver, resultUri)

            val result = mainViewModel.androidServicesInteractor.decodeWithZxing( bitmap)
            handleResult(result)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openCamera()
            else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            }
            //showCameraSnackBar()
        }
    }
}