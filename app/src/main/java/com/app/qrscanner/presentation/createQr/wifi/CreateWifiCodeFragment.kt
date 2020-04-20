package com.app.qrscanner.presentation.createQr.wifi

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat.getColor
import com.app.qrscanner.R
import com.app.qrscanner.presentation.global.BaseFragment
import com.app.qrscanner.presentation.global.CreateCodeBaseFragment
import com.app.qrscanner.utils.showToast
import kotlinx.android.synthetic.main.fragment_create_wifi_code.*
import net.glxn.qrgen.core.scheme.Schema
import net.glxn.qrgen.core.scheme.Wifi

class CreateWifiCodeFragment : CreateCodeBaseFragment(), View.OnClickListener {
    private lateinit var btnToUnfocus: Button

    override val layoutRes = R.layout.fragment_create_wifi_code
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initButtons()
    }

    private fun initButtons() {
        btn0.setOnClickListener(this)
        btn1.setOnClickListener(this)
        btn2.setOnClickListener(this)

        btnToUnfocus = btn0

    }
    private fun checkInputs(): Boolean {
        if (editText2.text.isEmpty() &&
            passwordInput.editText!!.text.isEmpty() ) {
            "Введите текст".showToast(context!!)
            return false
        }
        return true
    }

    override fun createCode(): Pair<String, Schema> {
        if (checkInputs()) {
            val result = Wifi()
            result.authentication = btnToUnfocus.text.toString()
            result.ssid = editText2.text.toString()
            result.psk = passwordInput.editText!!.text.toString()
            return Pair(result.generateString(), result)
        }
        return Pair("", Wifi())
    }

    override fun onClick(btn: View?) {
        if (btn != null) {

            when (btn.id) {
                R.id.btn0 -> {
                    setFocus(btn0)
                    showPasswordField()
                    btnToUnfocus = btn0

                }
                R.id.btn1 -> {
                    setFocus(btn1)
                    showPasswordField()
                    btnToUnfocus = btn1
                }
                R.id.btn2 -> {
                    setFocus(btn2)
                    hidePasswordField()
                    btnToUnfocus = btn2
                }
            }
        }
    }

    private fun setFocus(btnToFocus: Button) {
        btnToUnfocus.setTextColor(getColor(context!!, R.color.colorPrimary))
        btnToUnfocus.setBackgroundColor(getColor(context!!, R.color.transparent))

        btnToFocus.setTextColor(Color.WHITE)
        btnToFocus.setBackgroundResource(R.drawable.rounded_gradient_shape)
        btnToUnfocus = btnToFocus
    }

    private fun hidePasswordField() {
        passwordTextView.visibility = View.GONE
        passwordInput.visibility = View.GONE
    }

    private fun showPasswordField() {
        passwordTextView.visibility = View.VISIBLE
        passwordInput.visibility = View.VISIBLE
    }
}