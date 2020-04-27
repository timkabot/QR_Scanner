package com.app.qrscanner.presentation.createQr.contact

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import com.app.qrscanner.R
import com.app.qrscanner.presentation.global.CreateCodeBaseFragment
import com.app.qrscanner.utils.showToast
import com.app.qrscanner.utils.whenNotNull
import com.mindorks.editdrawabletext.DrawablePosition
import com.mindorks.editdrawabletext.onDrawableClickListener
import kotlinx.android.synthetic.main.fragment_create_contact_code.*
import net.glxn.qrgen.core.scheme.Schema
import net.glxn.qrgen.core.scheme.SchemeUtil
import net.glxn.qrgen.core.scheme.VCard

class CreateContactFragment : CreateCodeBaseFragment() {
    override val layoutRes = R.layout.fragment_create_contact_code
    private val REQUEST_CONTACT_NAME = 1
    private var contactUri: Uri? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        createButton.setOnClickListener {
            getMyActivity().createButtonOnClick()
        }
        nameInput.setDrawableClickListener(object : onDrawableClickListener {
            override fun onClick(target: DrawablePosition) {
                when (target) {
                    DrawablePosition.RIGHT -> {
                        checkContactPermission()
                    }
                }
            }
        })
    }

    fun checkContactPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                2
            )
        } else openPickContactDialog()
    }

    private fun openPickContactDialog() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        startActivityForResult(intent, REQUEST_CONTACT_NAME)
    }

    private fun checkInputs(): Boolean {
        if (nameInput.text!!.isEmpty() &&
            phoneInput.text!!.isEmpty() &&
            emailInput.text!!.isEmpty()
        ) {
            getString(R.string.enter_information).showToast(context!!)
            return false
        }
        return true
    }

    override fun createCode(): Pair<String, Schema> {
        val card = VCard()
        if (checkInputs()) {
            whenNotNull(nameInput.text.toString()) {
                card.name = nameInput.text.toString()
            }
            whenNotNull(phoneInput.text.toString()) {
                card.phoneNumber = phoneInput.text.toString()
            }
            whenNotNull(emailInput.text.toString()) {
                card.email = emailInput.text.toString()
            }
            println(card)
            println(card.name)
            val sb = StringBuilder()
            sb.append("Name").append(":").append(card.name)
            return Pair(card.generateString(), card)
        }
        return Pair("", card)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        contactUri = data!!.data

        if (requestCode == REQUEST_CONTACT_NAME && resultCode == Activity.RESULT_OK && contactUri != null)
            getPhoneNumber()
    }

    private fun getPhoneNumber() {

        val cursor = activity!!.contentResolver.query(contactUri!!, null, null, null, null)
        if (cursor!!.moveToFirst()) {
            val phoneNumber =
                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            phoneInput.setText(phoneNumber)

            val name =
                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            nameInput.setText(name)

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openPickContactDialog()
            } else {
                getString(R.string.contacts_permission_required).showToast(context!!)
            }
        }
    }
}