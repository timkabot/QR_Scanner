package com.app.qrscanner.presentation.showQr

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.qrscanner.R
import com.app.qrscanner.domain.entities.SerializableResult
import com.app.qrscanner.presentation.global.BaseFragment
import com.app.qrscanner.utils.argument
import com.app.qrscanner.utils.createQR
import kotlinx.android.synthetic.main.fragment_show_created_code.*

class ShowCreatedCodeFragment : BaseFragment() {
    override val layoutRes = R.layout.fragment_show_created_code
    private val qrCodeValue by argument(QR_CODE_VALUE, "")
    private val result by argument<SerializableResult>(RESULT_VALUE, null)


    private lateinit var qrBitmap: Bitmap
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        qrBitmap = createQR(qrCodeValue)
        qrCodeImageView.setImageBitmap(qrBitmap)

        textView.text = qrCodeValue
        initListeners()
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
                }
            }
            shareImage(qrBitmap)
        }

    }

    private fun showDialog() {
        context?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = layoutInflater
            builder.setTitle("With EditText")
            val dialogLayout = inflater.inflate(R.layout.dialog_save_code, null)
            val editText = dialogLayout.findViewById<EditText>(R.id.editText)
            builder.setView(dialogLayout)
            builder.setPositiveButton("Save") { _, _ ->
                saveImage(qrBitmap, editText.text.toString())
                Toast.makeText(
                    context,
                    "Saved QR code to gallery with name" + editText.text.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
            builder.show()
        }
    }

    // Method to save an image to gallery and return uri
    private fun saveImage(bitmap: Bitmap, title: String): Uri {
        val savedImageURL = MediaStore.Images.Media.insertImage(
            activity?.contentResolver,
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
        private const val RESULT_VALUE = "result_value"

        fun create(qrCodeValue: String, result: SerializableResult) =
            ShowCreatedCodeFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(QR_CODE_VALUE, qrCodeValue)
                    putSerializable(RESULT_VALUE, result)
                }
            }
        fun create(qrCodeValue: String) =
            ShowCreatedCodeFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(QR_CODE_VALUE, qrCodeValue)
                }
            }
    }
}