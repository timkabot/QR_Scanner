package com.app.qrscanner.presentation.createQr.phone

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import androidx.core.content.ContextCompat.checkSelfPermission
import com.app.qrscanner.R
import com.app.qrscanner.presentation.global.CreateCodeBaseFragment
import com.app.qrscanner.utils.showToast
import com.mindorks.editdrawabletext.DrawablePosition
import com.mindorks.editdrawabletext.onDrawableClickListener
import kotlinx.android.synthetic.main.fragment_create_phone_code.*
import net.glxn.qrgen.core.scheme.Schema
import net.glxn.qrgen.core.scheme.Telephone


class CreatePhoneCodeFragment : CreateCodeBaseFragment() {
    override val layoutRes = R.layout.fragment_create_phone_code
    private val REQUEST_PHONE_NUMBER = 1

    private var contactUri: Uri? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        phoneInput.setDrawableClickListener(object : onDrawableClickListener {
            override fun onClick(target: DrawablePosition) {
                when (target) {
                    DrawablePosition.RIGHT -> {
                        checkContactPermission()
                    }
                }
            }
        })
    }

    private fun openPickContactDialog() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        startActivityForResult(intent, REQUEST_PHONE_NUMBER)
    }

    fun checkContactPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                context!!,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                124
            )
        } else openPickContactDialog()
    }

    private fun checkInputs(): Boolean {
        if (phoneInput.text.isEmpty()) {
            "Введите номер телефона".showToast(context!!)
            return false
        }
        return true
    }

    override fun createCode(): Pair<String, Schema> {
        if (checkInputs()) {
            val result = Telephone()
            result.telephone = phoneInput.text.toString()
            return Pair(result.generateString(), Telephone())

        }
        return Pair("", Telephone())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        contactUri = data!!.data

        if (requestCode == REQUEST_PHONE_NUMBER && resultCode == Activity.RESULT_OK && contactUri != null)
            getPhoneNumber()
    }

    private fun getPhoneNumber() {

        val cursor = activity!!.contentResolver.query(contactUri!!, null, null, null, null)
        if (cursor!!.moveToFirst()) {
            val phoneNumber =
                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            phoneInput.setText(phoneNumber)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 124) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openPickContactDialog()
            } else {
                "Необходимо разрешение на чтение контактов для импорта".showToast(context!!)
            }
        }
    }
}