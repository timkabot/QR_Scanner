package com.app.qrscanner.presentation.createQr.card

import com.app.qrscanner.R
import com.app.qrscanner.presentation.global.CreateCodeBaseFragment
import com.app.qrscanner.utils.showToast
import com.app.qrscanner.utils.whenNotNull
import kotlinx.android.synthetic.main.fragment_create_mycard_code.*
import net.glxn.qrgen.core.scheme.Schema
import net.glxn.qrgen.core.scheme.VCard

class CreateCardCodeFragment : CreateCodeBaseFragment() {
    override val layoutRes = R.layout.fragment_create_mycard_code
    private fun checkInputs(): Boolean {
        if (nameInput.editText!!.text.isEmpty() &&
            phoneInput.text.isEmpty() &&
            addressInput.text.isEmpty() &&
            orgInput.text.isEmpty() &&
            noteInput.text.isEmpty()
        ) {
            "Введите информацию".showToast(context!!)
            return false
        }
        return true
    }

    override fun createCode(): Pair<String, Schema> {
        val card = VCard()
        if (checkInputs()) {
            whenNotNull(nameInput.editText!!.text.toString()) {
                card.name = nameInput.editText!!.text.toString()
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
}