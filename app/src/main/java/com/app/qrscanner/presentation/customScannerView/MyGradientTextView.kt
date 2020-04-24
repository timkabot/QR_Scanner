package com.app.qrscanner.presentation.customScannerView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.widget.TextView
import com.veeyaar.supergradienttextview.R
import com.app.qrscanner.R as RR

@SuppressLint("AppCompatCustomView")
class MyGradientTextView : TextView {
    private var isVertical = false
    private var startColor = this.resources.getColor(RR.color.colorPrimaryDark)
    private var endColor = this.resources.getColor(RR.color.colorPrimary)

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(
        context: Context,
        attrs: AttributeSet?
    ) {
        if (attrs != null) {
            val attributes =
                context.obtainStyledAttributes(attrs, R.styleable.GradientTextView)
            isVertical = attributes.getBoolean(R.styleable.GradientTextView_isVertical, false)
            startColor = attributes.getColor(R.styleable.GradientTextView_endColor, startColor)
            endColor = attributes.getColor(R.styleable.GradientTextView_startColor, endColor)
            attributes.recycle()
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            val paint: Paint = paint
            val width = paint.measureText(text.toString())
            if (isVertical) {
                getPaint().shader = LinearGradient(
                    0f, 0f, width, lineHeight.toFloat(),
                    endColor,
                    startColor,
                    Shader.TileMode.CLAMP
                )
            } else {
                getPaint().shader = LinearGradient(
                    0f, 0f, 0f, lineHeight.toFloat(),
                    endColor,
                    startColor,
                    Shader.TileMode.CLAMP
                )
            }
        }
    }
}