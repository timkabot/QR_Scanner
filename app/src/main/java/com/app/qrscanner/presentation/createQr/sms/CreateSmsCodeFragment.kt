package com.app.qrscanner.presentation.createQr.sms

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils
import androidx.core.content.ContextCompat
import com.app.qrscanner.R
import com.app.qrscanner.presentation.global.CreateCodeBaseFragment
import com.app.qrscanner.utils.showToast
import com.mindorks.editdrawabletext.DrawablePosition
import com.mindorks.editdrawabletext.onDrawableClickListener
import kotlinx.android.synthetic.main.fragment_create_sms_code.*
import net.glxn.qrgen.core.scheme.SMS
import net.glxn.qrgen.core.scheme.Schema

class CreateSmsCodeFragment : CreateCodeBaseFragment() {
    override val layoutRes = R.layout.fragment_create_sms_code
    private var contactUri: Uri? = null
    private val REQUEST_PHONE_NUMBER = 1

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
        createButton.setOnClickListener {
            getMyActivity().createButtonOnClick()
        }
    }
    fun getPhoneNumber() {
        val cursor = activity!!.contentResolver.query(contactUri!!, null, null, null, null)
        if (cursor!!.moveToFirst()) {
            val phoneNumber =
                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            phoneInput.setText(phoneNumber)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        contactUri = data!!.data

        if (requestCode == REQUEST_PHONE_NUMBER && resultCode == Activity.RESULT_OK && contactUri != null)
            getPhoneNumber()
    }
    private fun openPickContactDialog() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        startActivityForResult(intent, REQUEST_PHONE_NUMBER)
    }
    fun checkContactPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
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
    override fun createCode(): Pair<String, Schema> {
        val sms = SMS()
        if (phoneInput.text.isEmpty())
            getString(R.string.enter_phone_number).showToast(context!!)
        else if (!PhoneNumberUtils.isGlobalPhoneNumber((phoneInput.text.toString()))) {
            getString(R.string.wrong_phone_format_entered).showToast(context!!)
        } else {
            sms.number = phoneInput.text.toString()
            sms.subject = contentInput.text.toString()
            println("SMS ${sms.generateString()}")
            return Pair(sms.generateString(), sms)
        }
        return Pair("", sms)
    }

}