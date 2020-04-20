package com.app.qrscanner.presentation.createQr.spotify


import com.app.qrscanner.R
import com.app.qrscanner.presentation.global.CreateCodeBaseFragment
import com.app.qrscanner.utils.showToast
import kotlinx.android.synthetic.main.fragment_create_spotify_code.*
import net.glxn.qrgen.core.scheme.Schema
import net.glxn.qrgen.core.scheme.Url

class CreateSpotifyCodeFragment : CreateCodeBaseFragment() {
    override val layoutRes = R.layout.fragment_create_spotify_code

    private fun checkInputs(): Boolean {
        if (artistInput.text.isEmpty() && songInput.text.isEmpty()) {
            "Введите информацию".showToast(context!!)
            return false
        }
        return true
    }

    override fun createCode(): Pair<String, Schema> {
        if (checkInputs()) {
            val result = Url()
            result.url = "spotify:${artistInput.text}:spotify:track:${songInput.text}/"
            return Pair(result.generateString(), result)
        }
        return Pair("", Url())
    }
}