package com.app.qrscanner.presentation.createQr.twitter


import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import com.app.qrscanner.R
import com.app.qrscanner.presentation.global.BaseFragment
import com.app.qrscanner.presentation.global.CreateCodeBaseFragment
import com.app.qrscanner.utils.showToast
import kotlinx.android.synthetic.main.fragment_create_twitter_code.*
import net.glxn.qrgen.core.scheme.Schema
import net.glxn.qrgen.core.scheme.Url

class CreateTwitterCodeFragment : CreateCodeBaseFragment(), View.OnClickListener{
    override val layoutRes = R.layout.fragment_create_twitter_code

    private lateinit var btnToUnfocus: Button
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initButtons()
    }

    private fun initButtons() {
        btn0.setOnClickListener(this)
        btn1.setOnClickListener(this)
        btnToUnfocus = btn0

    }
    private fun checkInputs(): Boolean {
        if (editText2.text.isEmpty()) {
            "Введите информацию".showToast(context!!)
            return false
        }
        return true
    }

    override fun createCode(): Pair<String, Schema> {
        if (checkInputs()) {
            val result = Url()
            result.url = editText2.text.toString()
            if(btnToUnfocus == btn0){
                result.url = "twitter.com/${editText2.text}"
            }
            return Pair(result.generateString(), result)
        }
        return Pair("", Url())
    }
    override fun onClick(btn: View?) {
        if (btn != null) {

            when (btn.id) {
                R.id.btn0 -> {
                    setFocus(btn0)
                    btnToUnfocus = btn0

                }
                R.id.btn1 -> {
                    setFocus(btn1)
                    btnToUnfocus = btn1
                }
            }
        }
    }

    private fun setFocus(btnToFocus: Button) {
        btnToUnfocus.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
        btnToUnfocus.setBackgroundColor(ContextCompat.getColor(context!!, R.color.transparent))

        btnToFocus.setTextColor(Color.WHITE)
        btnToFocus.setBackgroundResource(R.drawable.rounded_gradient_shape)
        btnToUnfocus = btnToFocus
    }
}