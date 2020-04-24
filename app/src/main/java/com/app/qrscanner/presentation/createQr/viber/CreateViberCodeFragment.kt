package com.app.qrscanner.presentation.createQr.viber

import android.os.Bundle
import com.app.qrscanner.R
import com.app.qrscanner.presentation.global.CreateCodeBaseFragment
import com.app.qrscanner.utils.showToast
import kotlinx.android.synthetic.main.fragment_create_viber_code.*
import net.glxn.qrgen.core.scheme.Schema
import net.glxn.qrgen.core.scheme.Url

class CreateViberCodeFragment : CreateCodeBaseFragment(){
    override val layoutRes = R.layout.fragment_create_viber_code
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ccp.registerCarrierNumberEditText(editTextCarrierNumber)

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
            result.url = "viber://pa?chatURI=${ccp.fullNumber}"
            return Pair("viber://pa?chatURI=${ccp.fullNumber}", result)
        }
        println("${ccp.fullNumber} + ${editTextCarrierNumber.text.toString()}")
        return Pair("", Url())
    }

}