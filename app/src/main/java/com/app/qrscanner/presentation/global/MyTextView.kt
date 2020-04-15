package com.app.qrscanner.presentation.global


import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.TextView
import com.app.qrscanner.R

/**
 * Created by stepangoncarov on 08/06/16.
 */
class MyTextView @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    gefStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(ctx, attrs, gefStyleAttr) {

    var shader: Shader? = null
    val shaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = measuredHeight.toFloat()
        val width = measuredWidth.toFloat()
        val shader = LinearGradient(
            0f, 0f, width, height,
            intArrayOf(context!!.getColor(R.color.colorPrimaryDark), context!!.getColor(R.color.colorPrimary)),
            null,
            Shader.TileMode.CLAMP
        )
        this.shader = shader
        shaderPaint.shader = shader
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }
}