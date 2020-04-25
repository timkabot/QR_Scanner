package com.app.qrscanner.presentation.createQr.youtube

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import com.app.qrscanner.R
import com.app.qrscanner.presentation.global.BaseFragment
import com.app.qrscanner.presentation.global.CreateCodeBaseFragment
import com.app.qrscanner.utils.showToast
import kotlinx.android.synthetic.main.fragment_create_youtube_code.*
import net.glxn.qrgen.core.scheme.Schema
import net.glxn.qrgen.core.scheme.Url
import net.glxn.qrgen.core.scheme.YouTube

class CreateYoutubeCodeFragment : CreateCodeBaseFragment(), View.OnClickListener {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initButtons()
        createButton.setOnClickListener {
            getMyActivity().createButtonOnClick()
        }
    }

    override val layoutRes = R.layout.fragment_create_youtube_code
    private lateinit var btnToUnfocus: Button

    private fun initButtons() {
        btn0.setOnClickListener(this)
        btn1.setOnClickListener(this)
        btn2.setOnClickListener(this)

        btnToUnfocus = btn0

    }
    private fun checkInputs(): Boolean {
        if (editText2.text.isEmpty()) {
            getString(R.string.enter_text).showToast(context!!)
            return false
        }
        return true
    }

    override fun createCode(): Pair<String, Schema> {
        if (checkInputs()) {
            val result = Url()
            if(btnToUnfocus == btn0) result.url = editText2.text.toString()
            if(btnToUnfocus == btn1) result.url = "http://www.youtube.com/watch?v=:${editText2.text}"
            if(btnToUnfocus == btn2)result.url = "https://www.youtube.com/channel/${editText2.text}"

            return Pair(result.generateString(), result)
        }
        return Pair("", Url())
    }

    override fun onClick(btn: View?) {
        if (btn != null) {

            when (btn.id) {
                R.id.btn0 -> {
                    setFocus(btn0)
                    editText2.hint = getString(R.string.enter_youtube_link)
                }
                R.id.btn1 -> {
                    setFocus(btn1)
                    editText2.hint = getString(R.string.enter_video_id)

                }
                R.id.btn2 -> {
                    setFocus(btn2)
                    editText2.hint = getString(R.string.enter_channel_id)
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