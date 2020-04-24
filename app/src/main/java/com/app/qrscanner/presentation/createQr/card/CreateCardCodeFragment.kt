package com.app.qrscanner.presentation.createQr.card

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
import kotlinx.android.synthetic.main.fragment_create_mycard_code.*
import net.glxn.qrgen.core.scheme.Schema
import net.glxn.qrgen.core.scheme.VCard

class CreateCardCodeFragment : CreateCodeBaseFragment() {
    private val REQUEST_CONTACT = 1

    override val layoutRes = R.layout.fragment_create_mycard_code
    private var contactUri: Uri? = null

    private fun checkInputs(): Boolean {
        if (nameInput.text.isEmpty() &&
            phoneInput.text.isEmpty() &&
            addressInput.text.isEmpty() &&
            orgInput.text.isEmpty() &&
            noteInput.text.isEmpty()
        ) {
            getString(R.string.enter_information).showToast(context!!)
            return false
        }
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initListeners()
    }

    override fun createCode(): Pair<String, Schema> {
        val card = VCard()
        if (checkInputs()) {
            whenNotNull(nameInput.text.toString()) {
                card.name = nameInput.text.toString()
            }
            whenNotNull(emailInput.text.toString()) {
                card.email = emailInput.text.toString()
            }
            whenNotNull(phoneInput.text.toString()) {
                card.phoneNumber = phoneInput.text.toString()
            }
            whenNotNull(addressInput.text.toString()) {
                card.address = addressInput.text.toString()
            }
            whenNotNull(orgInput.text.toString()) { card.company = orgInput.text.toString() }
            whenNotNull(noteInput.text.toString()) { card.note = noteInput.text.toString() }
            return Pair(card.generateString(), card)
        }
        return Pair("", card)
    }

    fun initListeners() {
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
                124
            )
        } else openPickContactDialog()
    }


    private fun openPickContactDialog() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        startActivityForResult(intent, REQUEST_CONTACT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        contactUri = data!!.data

        if (requestCode == REQUEST_CONTACT && resultCode == Activity.RESULT_OK && contactUri != null)
            getContact()
    }

    private fun getContact() {
        val cursor = activity!!.contentResolver.query(contactUri!!, null, null, null, null)
        if (cursor!!.moveToFirst()) {
            val phoneNumber =
                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            phoneInput.setText(phoneNumber)

            val contactName =
                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            nameInput.setText(contactName)
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
                getString(R.string.contacts_permission_required).showToast(context!!)
            }
        }
    }
}