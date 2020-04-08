package com.app.qrscanner.presentation.settings

import android.os.Bundle
import com.app.qrscanner.R
import com.app.qrscanner.data.local.SettingsLocal
import com.app.qrscanner.presentation.global.BaseFragment
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment(){
    override val layoutRes = R.layout.fragment_settings

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vibrateSwitch.isChecked = SettingsLocal.vibrate
        copySwitch.isChecked = SettingsLocal.autoCopy
        initListeners()
    }

    private fun initListeners(){
        vibrateSwitch.setOnCheckedChangeListener { _, isChecked ->
            SettingsLocal.vibrate = isChecked
        }

        copySwitch.setOnCheckedChangeListener { _, isChecked ->
            SettingsLocal.autoCopy = isChecked
        }
    }
}