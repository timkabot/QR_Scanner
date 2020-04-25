package com.app.qrscanner.presentation.createQr.whatsapp

import android.os.Bundle
import com.app.qrscanner.R
import com.app.qrscanner.presentation.global.CreateCodeBaseFragment
import com.app.qrscanner.utils.showToast
import kotlinx.android.synthetic.main.fragment_create_whatsapp_code.*
import kotlinx.android.synthetic.main.fragment_create_whatsapp_code.ccp
import kotlinx.android.synthetic.main.fragment_create_whatsapp_code.editTextCarrierNumber
import net.glxn.qrgen.core.scheme.Schema
import net.glxn.qrgen.core.scheme.Url

class CreateWhatsAppCodeFragment: CreateCodeBaseFragment(){
    override val layoutRes = R.layout.fragment_create_whatsapp_code
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ccp.registerCarrierNumberEditText(editTextCarrierNumber)
        createButton.setOnClickListener {
            getMyActivity().createButtonOnClick()
        }
    }

    private fun checkInputs(): Boolean {
        if (editTextCarrierNumber.text.isEmpty()) {
            getString(R.string.enter_text).showToast(context!!)
            return false
        }
        return true
    }

    override fun createCode(): Pair<String, Schema> {
        if (checkInputs()) {
            val result = Url()
            return Pair("whatsapp://send?abid=${ccp.fullNumber}", result)
        }
        return Pair("", Url())
    }
}