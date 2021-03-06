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
        createButton.setOnClickListener {
            getMyActivity().createButtonOnClick()
        }
    }

    private fun initButtons() {
        btn0.setOnClickListener(this)
        btn1.setOnClickListener(this)
        btnToUnfocus = btn0

    }
    private fun checkInputs(): Boolean {
        if (editText2.text.isEmpty()) {
            getString(R.string.enter_information).showToast(context!!)
            return false
        }
        return true
    }

    override fun createCode(): Pair<String, Schema> {
        if (checkInputs()) {
            if(btnToUnfocus == btn0){
                return Pair( "twitter.com/${editText2.text}", Url())
            }
            if(btnToUnfocus == btn1) return Pair(editText2.text.toString(), Url())
        }
        return Pair("", Url())
    }
    override fun onClick(btn: View?) {
        if (btn != null) {

            when (btn.id) {
                R.id.btn0 -> {
                    setFocus(btn0)
                    btnToUnfocus = btn0
                    editText2.hint = getString(R.string.enter_twitter_username)

                }
                R.id.btn1 -> {
                    setFocus(btn1)
                    btnToUnfocus = btn1
                    editText2.hint = getString(R.string.enter_url)

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