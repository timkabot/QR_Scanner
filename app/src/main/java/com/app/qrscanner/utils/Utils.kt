package com.app.qrscanner.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.text.BoringLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.google.zxing.qrcode.QRCodeWriter
import net.glxn.qrgen.android.QRCode
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun String.showToast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()

}

fun CharSequence?.isNotNullOrEmpty(): Boolean {
    if(this == null) return false
    return length > 0
}


fun List<String>?.isNotNullOrEmpty(): Boolean {
    if(this == null || size<1) return false
    return get(0).isNotEmpty()
}
fun List<String>.getValue(): String {
    return get(0)
}

inline fun <T : Any, R> whenNotNull(input: T?, callback: (T) -> R): R? {
    return input?.let(callback)
}

fun generateNotInstalledAppError(appName: String, context: Context){
    "Сначала установите приложение ${appName} пожалуйста".showToast(context)
}
fun createQR(value: String, width: Int = 512, height: Int = 512): Bitmap {
    val writer = QRCodeWriter()
    return QRCode.from(value)
        .withCharset("UTF-8")
        .withSize(1024,1024)
        .bitmap()
}

inline fun <reified T> extra(
    key: String,
    defaultValue: T? = null
): ReadWriteProperty<Activity, T> =
    BundleExtractorDelegate { thisRef ->
        extractFromBundle(
            bundle = thisRef.intent?.extras,
            key = key,
            defaultValue = defaultValue
        )
    }

inline fun <reified T> argument(
    key: String,
    defaultValue: T? = null
): ReadWriteProperty<Fragment, T> =
    BundleExtractorDelegate { thisRef ->
        extractFromBundle(
            bundle = thisRef.arguments,
            key = key,
            defaultValue = defaultValue
        )
    }

inline fun <reified T> extractFromBundle(
    bundle: Bundle?,
    key: String,
    defaultValue: T? = null
): T {
    val result = bundle?.get(key) ?: defaultValue
    if (result != null && result !is T) {
        throw ClassCastException("Property $key has different class type")
    }
    return result as T
}

class BundleExtractorDelegate<R, T>(private val initializer: (R) -> T) : ReadWriteProperty<R, T> {

    private object EMPTY

    private var value: Any? = EMPTY

    override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        this.value = value
    }

    override fun getValue(thisRef: R, property: KProperty<*>): T {
        if (value == EMPTY) {
            value = initializer(thisRef)
        }
        @Suppress("UNCHECKED_CAST")
        return value as T
    }
}