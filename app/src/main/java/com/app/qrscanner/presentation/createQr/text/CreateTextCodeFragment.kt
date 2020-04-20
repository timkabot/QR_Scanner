package com.app.qrscanner.presentation.createQr.text


import com.app.qrscanner.R
import com.app.qrscanner.presentation.global.CreateCodeBaseFragment
import com.app.qrscanner.utils.showToast
import kotlinx.android.synthetic.main.fragment_create_text_code.*
import net.glxn.qrgen.core.scheme.Schema
import net.glxn.qrgen.core.scheme.Url

class CreateTextCodeFragment : CreateCodeBaseFragment(){
    override val layoutRes = R.layout.fragment_create_text_code

    private fun checkInputs(): Boolean {
        if (textInput.text.isEmpty()) {
            "Введите текст".showToast(context!!)
            return false
        }
        return true
    }

    override fun createCode(): Pair<String, Schema> {
        if (checkInputs()) {
            return Pair( textInput.text.toString(),  Url())
        }
        return Pair("", Url())
    }
}