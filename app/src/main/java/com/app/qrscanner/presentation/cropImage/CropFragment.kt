package com.app.qrscanner.presentation.cropImage

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ProgressBar
import com.app.qrscanner.R
import com.app.qrscanner.Screens
import com.app.qrscanner.data.local.SettingsLocal
import com.app.qrscanner.domain.entities.Code
import com.app.qrscanner.domain.entities.CodeStatus
import com.app.qrscanner.presentation.ContainerActivity
import com.app.qrscanner.presentation.MainViewModel
import com.app.qrscanner.presentation.customViews.MyZxing
import com.app.qrscanner.presentation.global.BaseFragment
import com.app.qrscanner.utils.argument
import com.app.qrscanner.utils.showToast
import com.google.zxing.Result
import com.google.zxing.client.result.ResultParser
import com.takusemba.cropme.OnCropListener
import kotlinx.android.synthetic.main.activity_main_container.*
import kotlinx.android.synthetic.main.crop_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_crop.*
import org.koin.android.viewmodel.ext.android.viewModel

class CropFragment : BaseFragment(), MyZxing.ResultHandler{
    override val layoutRes = R.layout.fragment_crop
    private val imageUriString by argument("uri", "")
    private val mainVM: MainViewModel by viewModel()
    private val PICK_IMAGE_GALLERY_REQUEST_CODE = 100
    private val activity
        get() = getActivity() as ContainerActivity

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(imageUriString != ""){
            val imageUri = Uri.parse(imageUriString)
            cropView.setUri(imageUri)
        }
        initListeners()
    }
    private fun initListeners(){
        cropImage.setOnClickListener {
            if(!cropView.isOffFrame())
            {
                cropView.crop()
                progressBar.visibility = ProgressBar.VISIBLE
            }
            else {
                "Область вне картинки".showToast(context!!)
            }
        }

        cropView.addOnCropListener(object : OnCropListener {
            override fun onSuccess(bitmap: Bitmap) {
                progressBar.visibility = ProgressBar.INVISIBLE
                val result = mainVM.androidServicesInteractor.decodeWithZxing(bitmap)
                handleResult(result)
            }

            override fun onFailure(e: Exception) {
                progressBar.visibility = ProgressBar.INVISIBLE
                mainVM.router.exit()
                "Не получилось обрезать картинку".showToast(context!!)
            }
        })

        cropToolbar.backButton.setOnClickListener { openGallery() }
        cropToolbar.closeButton.setOnClickListener { mainVM.router.exit() }
    }
    private fun openGallery() {
        val gallery =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, PICK_IMAGE_GALLERY_REQUEST_CODE)
    }

    companion object {
        fun create(uri: Uri) = CropFragment().apply {
            arguments = Bundle().apply {
                putString("uri", uri.toString())
            }
        }
    }
    private fun saveCodeToDatabase(res: Result) {
        val parsedResult = ResultParser.parseResult(res)
        val code = Code(data = res.text, status = CodeStatus.SCANNED).apply {
            type = mainVM.parsedResultInteractor.getCodeTypeForParsedResult(parsedResult)
            shortDescription = mainVM.parsedResultInteractor.getInfoForParsedResult(parsedResult)
            result = res
        }
        if (code.shortDescription.isEmpty())
            code.shortDescription = res.text
        mainVM.databaseInteractor.saveCodeInDatabase(code)
    }

    override fun handleResult(p0: Result?) {
        p0?.let {
            println("Scanned ${p0.text}")
            if (SettingsLocal.vibrate) mainVM.androidServicesInteractor.vibrate(activity)
            if (SettingsLocal.autoCopy) mainVM.androidServicesInteractor.copyToClipBoard(p0.text, activity)
            if (SettingsLocal.beep) mainVM.androidServicesInteractor.playSound(activity)

            saveCodeToDatabase(p0)
            mainVM.lastScannedResult = p0
            mainVM.router.replaceScreen(Screens.ShowScannedQRScreen)
        }
        if (p0 == null) {
            "QR код не был найден".showToast(context!!)
            mainVM.router.exit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_GALLERY_REQUEST_CODE) {
            data?.let {
                val resultUri = it.data!!
                cropView.setUri(resultUri)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        activity.bottomNavigationView.visibility = View.GONE
        activity.customToolbar.visibility = View.GONE
        activity.fab.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        activity.bottomNavigationView.visibility = View.VISIBLE
        activity.customToolbar.visibility = View.VISIBLE
        activity.fab.visibility = View.VISIBLE
    }
}