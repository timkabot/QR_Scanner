package com.app.qrscanner.domain.entities

import com.google.zxing.Result
import java.io.Serializable

data class SerializableResult(val result: Result? = null) : Serializable