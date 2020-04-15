package com.app.qrscanner.data.local

import com.chibatching.kotpref.KotprefModel

object SettingsLocal : KotprefModel() {
    var vibrate by booleanPref(default = false)
    var autoCopy by booleanPref(default = false)
    var beep by booleanPref(default = false)
}