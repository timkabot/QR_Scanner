package com.app.qrscanner.presentation.createQr.youtube

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import com.app.qrscanner.R
import com.app.qrscanner.presentation.global.BaseFragment
import kotlinx.android.synthetic.main.fragment_create_youtube_code.*

class CreateYoutubeCodeFragment : BaseFragment(), View.OnClickListener {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initButtons()
    }

    override val layoutRes = R.layout.fragment_create_youtube_code
    private lateinit var btnToUnfocus: Button

    private fun initButtons() {
        btn0.setOnClickListener(this)
        btn1.setOnClickListener(this)
        btn2.setOnClickListener(this)

        btnToUnfocus = btn0

    }

    override fun onClick(btn: View?) {
        if (btn != null) {

            when (btn.id) {
                R.id.btn0 -> {
                    setFocus(btn0)
                }
                R.id.btn1 -> {
                    setFocus(btn1)
                }
                R.id.btn2 -> {
                    setFocus(btn2)
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