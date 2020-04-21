package com.app.qrscanner.presentation.settings

import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Shader.TileMode
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.app.qrscanner.R
import com.app.qrscanner.data.local.SettingsLocal
import com.app.qrscanner.presentation.ContainerActivity
import com.app.qrscanner.presentation.global.BaseFragment
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : BaseFragment() {
    override val layoutRes = R.layout.fragment_settings
    private val activity
        get() = getActivity() as ContainerActivity

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initValues()

        initListeners()
        setGradientForTextViews()
    }

    override fun onResume() {
        changeToolbar()
        super.onResume()
    }

    private fun initValues() {
        vibrateSwitch.isChecked = SettingsLocal.vibrate
        copySwitch.isChecked = SettingsLocal.autoCopy
        soundSwitch.isChecked = SettingsLocal.beep
    }

    private fun changeToolbar() {
        ContainerActivity.setAppBatTitle("Настройки", activity)
        ContainerActivity.changeSettingButtonVisibility(activity, View.GONE)
        ContainerActivity.changeAdsButtonVisibility(activity, View.VISIBLE)
        ContainerActivity.changeNoAdsButtonVisibility(activity, View.VISIBLE)
        ContainerActivity.changeBackButtonVisibility(activity, View.VISIBLE)
        ContainerActivity.changeCreateQrButtonVisibility(activity, View.GONE)
    }

    private fun initListeners() {
        vibrateSwitch.setOnCheckedChangeListener { _, isChecked ->
            SettingsLocal.vibrate = isChecked
        }

        copySwitch.setOnCheckedChangeListener { _, isChecked ->
            SettingsLocal.autoCopy = isChecked
        }

        soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            SettingsLocal.beep = isChecked
        }

        rateUsText.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=vk")))
            } catch (e: Exception) {
            }
        }
        shareText.setOnClickListener {
            share()
        }


    }
    private fun share()
    {
        /* ACTION_SEND: Deliver some data to someone else.
         createChooser (Intent target, CharSequence title): Here, target- The Intent that the user will be selecting an activity to perform.
             title- Optional title that will be displayed in the chooser.
         Intent.EXTRA_TEXT: A constant CharSequence that is associated with the Intent, used with ACTION_SEND to supply the literal data to be sent.
         */
        val sendIntent = Intent();
        sendIntent.action = Intent.ACTION_SEND;
        sendIntent.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id=qrcode.reader.qrscanner.barcode.scanner.qrcodereader");
        sendIntent.setType("text/plain");
        Intent.createChooser(sendIntent,"Share via");
        startActivity(sendIntent);
    }
    private fun setGradientForTextViews() {
        val shader = LinearGradient(
            0f,
            8f,
            0f,
            18f,
            context!!.getColor(R.color.colorPrimaryDark),
            context!!.getColor(R.color.colorPrimary),
            TileMode.CLAMP
        )
        generalSettingsTextView.paint.shader = shader
        //helpTextView.paint.shader = shader
    }


}