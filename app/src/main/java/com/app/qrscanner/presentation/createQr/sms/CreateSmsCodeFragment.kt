package com.app.qrscanner.presentation.createQr.sms

import android.telephony.PhoneNumberUtils
import com.app.qrscanner.R
import com.app.qrscanner.presentation.global.CreateCodeBaseFragment
import com.app.qrscanner.utils.showToast
import kotlinx.android.synthetic.main.fragment_create_sms_code.*
import net.glxn.qrgen.core.scheme.SMS
import net.glxn.qrgen.core.scheme.Schema

class CreateSmsCodeFragment : CreateCodeBaseFragment() {
    override val layoutRes = R.layout.fragment_create_sms_code
    override fun createCode(): Pair<String, Schema> {
        val sms = SMS()
        if (phoneInput.text.isEmpty())
            "Введите номер".showToast(context!!)
        else if (!PhoneNumberUtils.isGlobalPhoneNumber((phoneInput.text.toString()))) {
            "Введён неправильный формат номера".showToast(context!!)
        } else {
            sms.number = phoneInput.text.toString()
            sms.subject = contentInput.text.toString()
            return Pair(sms.generateString(), sms)
        }
        return Pair("", sms)
    }
}