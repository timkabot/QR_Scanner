package com.app.qrscanner.presentation.scanQr

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import com.app.qrscanner.R
import com.app.qrscanner.Screens
import com.app.qrscanner.data.CodesRepository
import com.app.qrscanner.data.local.SettingsLocal
import com.app.qrscanner.domain.entities.Code
import com.app.qrscanner.domain.entities.CodeStatus
import com.app.qrscanner.domain.entities.SerializableResult
import com.app.qrscanner.presentation.ContainerActivity
import com.app.qrscanner.presentation.global.BaseFragment
import com.app.qrscanner.utils.HUAWEI
import com.app.qrscanner.utils.MY_CAMERA_REQUEST_CODE
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import kotlinx.android.synthetic.main.fragment_scan.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.Router


class ScanQrFragment : BaseFragment(), ZXingScannerView.ResultHandler {
    override val layoutRes = R.layout.fragment_scan
    private val codesRepository by inject<CodesRepository>()
    private val router by inject<Router>()
    private val activity
        get() = getActivity() as ContainerActivity

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setScannerProperties()
        initListeners()
    }

    private fun initListeners() {
        val showFlashDrawable =
            AppCompatResources.getDrawable(context!!, R.drawable.baseline_flash_on_white_48)
        val hideFlashDrawable =
            AppCompatResources.getDrawable(context!!, R.drawable.baseline_flash_off_white_48)

//        btn_flash.setOnClickListener {
//            if (btn_flash.icon == showFlashDrawable) {
//                qrCodeScanner.flash = true
//                btn_flash.icon = hideFlashDrawable
//            } else {
//                qrCodeScanner.flash = false
//                btn_flash.icon = showFlashDrawable
//            }
//        }
    }

    private fun changeToolbar(started: Boolean) {
        if (started) {

            ContainerActivity.setAppBatTitle("", activity)
            ContainerActivity.changeSettingButtonVisibility(activity, View.VISIBLE)
            ContainerActivity.changeAdsButtonVisibility(activity, View.VISIBLE)
            ContainerActivity.changeNoAdsButtonVisibility(activity, View.GONE)
            ContainerActivity.changeBackButtonVisibility(activity, View.GONE)
        } else {
            ContainerActivity.setAppBatTitle("", activity)
            ContainerActivity.changeBackButtonVisibility(activity, View.VISIBLE)

        }

    }

    private fun setScannerProperties() {
        qrCodeScanner.setAutoFocus(true)
        qrCodeScanner.setBorderLineLength(200)
        qrCodeScanner.setBorderStrokeWidth(10)
        qrCodeScanner.setLaserColor(R.color.colorAccent)
        if (Build.MANUFACTURER.equals(HUAWEI, ignoreCase = true))
            qrCodeScanner.setAspectTolerance(0.5f)
        qrCodeScanner.startCamera()
    }

    // Playing sound
    // will play button toggle sound on flash on / off
//    private fun playSound() {
//        if (isFlashOn) {
//            AudioPlayer.player = MediaPlayer.create(activity, R.raw.light_switch_off)
//        } else {
//            AudioPlayer.player = MediaPlayer.create(activity, R.raw.light_switch_on)
//        }
//        AudioPlayer.player.setOnCompletionListener(OnCompletionListener { player -> player.release() })
//        AudioPlayer.player.start()
//    }

    override fun onResume() {
        super.onResume()
        changeToolbar(true)
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

    private fun copyToClipBoard(textToCopy: String) {
        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("RANDOM UUID", textToCopy)
        clipboard.setPrimaryClip(clip)
    }

    private fun vibrate() {
        val vibrator = activity.application?.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(100)
    }

    private fun saveCodeInDatabase(code: Code) {
        Thread {
            codesRepository.insertCode(code)
        }.also {
            it.start()
        }
    }

    override fun handleResult(p0: Result?) {

        p0?.let {
            if (SettingsLocal.vibrate) vibrate()
            if (SettingsLocal.autoCopy) copyToClipBoard(p0.text)
            saveCodeInDatabase((Code(data = p0.text, status = CodeStatus.SCANNED)))
            val serializableResult = SerializableResult(p0)
            router.navigateTo(Screens.ShowScannedQRScreen(p0.text, serializableResult))
        }
    }

    private fun openCamera() {
        qrCodeScanner.startCamera()
        qrCodeScanner.setResultHandler(this)
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