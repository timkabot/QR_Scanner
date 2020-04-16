package com.app.qrscanner.presentation.settings

import android.graphics.LinearGradient
import android.graphics.Shader.TileMode
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