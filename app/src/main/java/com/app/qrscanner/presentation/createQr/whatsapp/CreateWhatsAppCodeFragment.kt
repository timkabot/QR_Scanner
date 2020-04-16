package com.app.qrscanner.presentation.createQr.whatsapp

import android.os.Bundle
import com.app.qrscanner.R
import com.app.qrscanner.presentation.global.BaseFragment
import kotlinx.android.synthetic.main.fragment_create_whatsapp_code.*

class CreateWhatsAppCodeFragment: BaseFragment(){
    override val layoutRes = R.layout.fragment_create_whatsapp_code
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ccp.registerCarrierNumberEditText(editTextCarrierNumber)

    }
}