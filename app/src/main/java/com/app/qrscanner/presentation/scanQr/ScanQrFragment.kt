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
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.getSystemService
import com.app.qrscanner.R
import com.app.qrscanner.data.CodesRepository
import com.app.qrscanner.data.local.SettingsLocal
import com.app.qrscanner.domain.entities.Code
import com.app.qrscanner.domain.entities.CodeType
import com.app.qrscanner.presentation.global.BaseFragment
import com.app.qrscanner.utils.HUAWEI
import com.app.qrscanner.utils.MY_CAMERA_REQUEST_CODE
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import kotlinx.android.synthetic.main.fragment_scan.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.android.ext.android.inject

class ScanQrFragment : BaseFragment(), ZXingScannerView.ResultHandler {
    override val layoutRes = R.layout.fragment_scan
    val codesRepository by inject<CodesRepository>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setScannerProperties()
        initListeners()
    }

    private fun initListeners(){
        val showFlashDrawable = AppCompatResources.getDrawable(context!!, R.drawable.baseline_flash_on_white_48)
        val hideFlashDrawable = AppCompatResources.getDrawable(context!!, R.drawable.baseline_flash_off_white_48)

        btn_flash.setOnClickListener {
            if(btn_flash.icon == showFlashDrawable)
            {
                qrCodeScanner.flash = true
                btn_flash.icon = hideFlashDrawable
            }
            else {
                qrCodeScanner.flash = false
                btn_flash.icon = showFlashDrawable
            }
        }
    }

    private fun setScannerProperties() {
        qrCodeScanner.setFormats(listOf(BarcodeFormat.QR_CODE))
        qrCodeScanner.setAutoFocus(true)
        qrCodeScanner.setLaserColor(R.color.colorAccent)
        qrCodeScanner.setMaskColor(R.color.colorAccent)
        if (Build.MANUFACTURER.equals(HUAWEI, ignoreCase = true))
            qrCodeScanner.setAspectTolerance(0.5f)
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context?.let {
                if (!isCameraPermissionGranted()) {
                    ActivityCompat.requestPermissions(activity as Activity, arrayOf(Manifest.permission.CAMERA),
                        MY_CAMERA_REQUEST_CODE)
                    return
                }
            }
        }

        qrCodeScanner.startCamera()
        qrCodeScanner.setResultHandler(this)
    }

    private fun isCameraPermissionGranted(): Boolean {
        val selfPermission = checkSelfPermission(context!!, Manifest.permission.CAMERA)
        return selfPermission == PackageManager.PERMISSION_GRANTED
    }

    override fun onPause() {
        super.onPause()
        qrCodeScanner.stopCamera()
    }

    private fun copyToClipBoard(textToCopy: String){
        val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("RANDOM UUID",textToCopy)
        clipboard.setPrimaryClip(clip)
    }

    private fun vibrate(){
        val vibrator = activity?.application?.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(100)
    }

    private fun saveCodeInDatabase(code: Code){
        Thread {
            codesRepository.insertCode(code)
        }.also {
            it.start()
        }
    }

    override fun handleResult(p0: Result?) {
        p0?.let {
            if(SettingsLocal.vibrate) vibrate()
            if(SettingsLocal.autoCopy) copyToClipBoard(p0.text)
            Toast.makeText(context,"${p0.text}, type: ${p0.barcodeFormat}",Toast.LENGTH_LONG).show()
            saveCodeInDatabase((Code(data = p0.text, type = CodeType.SCANNED)))
            resumeCamera()
        }
    }

    private fun openCamera() {
        qrCodeScanner.startCamera()
        qrCodeScanner.setResultHandler(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openCamera()
            else if (grantResults[0] == PackageManager.PERMISSION_DENIED){}
                //showCameraSnackBar()
        }
    }

    private fun resumeCamera() {
       // val handler = Handler()
        //handler.postDelayed({ qrCodeScanner.resumeCameraPreview(this) }, 2000)
    }
}