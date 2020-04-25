package com.app.qrscanner.presentation.createQr.mail

import android.os.Bundle
import com.app.qrscanner.R
import com.app.qrscanner.domain.entities.MailSchema
import com.app.qrscanner.presentation.global.BaseFragment
import com.app.qrscanner.presentation.global.CreateCodeBaseFragment
import com.app.qrscanner.utils.showToast
import com.app.qrscanner.utils.whenNotNull
import kotlinx.android.synthetic.main.fragment_create_email_code.*
import net.glxn.qrgen.core.scheme.EMail
import net.glxn.qrgen.core.scheme.Schema

class CreateMailCodeFragment : CreateCodeBaseFragment(){
    override val layoutRes = R.layout.fragment_create_email_code

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        createButton.setOnClickListener {
            getMyActivity().createButtonOnClick()
        }
    }
    private fun checkInputs(): Boolean {
        if (emailInput.text.isEmpty() &&
            subjectInput.text.isEmpty() &&
            contentInput.text.isEmpty()
        ) {
            getString(R.string.enter_information).showToast(context!!)
            return false
        }
        return true
    }

    override fun createCode(): Pair<String, Schema> {
        val email = MailSchema()
        if (checkInputs()) {
            whenNotNull(emailInput.text.toString()) {
                email.receiver = emailInput.text.toString()
            }
            whenNotNull(subjectInput.text.toString()) {
                email.subject = subjectInput.text.toString()
            }
            whenNotNull(contentInput.text.toString()) {
                email.body = contentInput.text.toString()
            }

            return Pair(email.generateString(), email)
        }
        return Pair("", email)
    }
}