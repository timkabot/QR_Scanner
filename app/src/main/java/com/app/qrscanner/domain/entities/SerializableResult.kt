package com.app.qrscanner.domain.entities

import android.os.Parcelable
import com.google.zxing.Result
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class SerializableResult( val result: Result? = null) : Serializable