package com.app.qrscanner.presentation.createQr.text


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import com.app.qrscanner.R
import com.app.qrscanner.presentation.global.CreateCodeBaseFragment
import com.app.qrscanner.utils.showToast
import kotlinx.android.synthetic.main.fragment_create.*
import kotlinx.android.synthetic.main.fragment_create_text_code.*
import net.glxn.qrgen.core.scheme.Schema
import net.glxn.qrgen.core.scheme.Url

class CreateTextCodeFragment : CreateCodeBaseFragment(){
    override val layoutRes = R.layout.fragment_create_text_code

    private fun checkInputs(): Boolean {
        if (textInput.text.isEmpty()) {
            getString(R.string.enter_text).showToast(context!!)
            return false
        }
        return true
    }
    fun initListeners(){
        pasteText.setOnClickListener {
            textInput.setText(pasteText.text)
        }
        imageView4.setOnClickListener {
            pasteText.text
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val clipboard =
            activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData: ClipData? = clipboard.primaryClip
        clipData?.apply {
            val textToPaste: String = this.getItemAt(0).text.toString().trim()
            if(textToPaste.isNotEmpty())
            {
                pasteText.text = textToPaste
                initListeners()
            }
        }


    }
    override fun createCode(): Pair<String, Schema> {
        if (checkInputs()) {
            return Pair( textInput.text.toString(),  Url())
        }
        return Pair("", Url())
    }
}