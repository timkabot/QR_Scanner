package com.app.qrscanner.presentation.createQr.instagram

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import com.app.qrscanner.R
import com.app.qrscanner.presentation.global.BaseFragment
import kotlinx.android.synthetic.main.fragment_create_instagram_code.*

class CreateInstagramCodeFragment : BaseFragment(), View.OnClickListener{
    override val layoutRes = R.layout.fragment_create_instagram_code

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

    override fun onClick(btn: View?) {
        if (btn != null) {

            when (btn.id) {
                R.id.btn0 -> {
                    setFocus(btn0)
                }
                R.id.btn1 -> {
                    setFocus(btn1)
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