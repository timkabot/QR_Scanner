package com.app.qrscanner.presentation.customScannerView

import android.content.Context
import android.graphics.*
import android.graphics.Paint.Join
import android.util.AttributeSet
import android.view.View
import com.app.qrscanner.R
import com.app.qrscanner.presentation.MainViewModel
import me.dm7.barcodescanner.core.DisplayUtils
import me.dm7.barcodescanner.core.IViewFinder
import me.dm7.barcodescanner.core.R.color
import me.dm7.barcodescanner.core.R.integer
import org.koin.android.viewmodel.ext.android.viewModel


class MyViewFinder : View, IViewFinder {
    var mFramingRect: Rect? = null
    private var scannerAlpha = 0
    private val mDefaultLaserColor: Int
    private val mDefaultMaskColor: Int
    private val mDefaultBorderColor: Int
    private val mDefaultBorderStrokeWidth: Int
    private val mDefaultBorderLineLength: Int
    protected var mLaserPaint: Paint? = null
    protected var mFinderMaskPaint: Paint? = null
    protected var mBorderPaint: Paint? = null
    protected var mBorderLineLength = 0
    protected var mSquareViewFinder = false
    private var mIsLaserEnabled = false
    private var mBordersAlpha = 0f
    private var mViewFinderOffset: Int

    constructor(context: Context?) : super(context) {
        mDefaultLaserColor = this.resources.getColor(color.viewfinder_laser)
        mDefaultMaskColor = this.resources.getColor(color.viewfinder_mask)
        mDefaultBorderColor = this.resources.getColor(color.viewfinder_border)
        mDefaultBorderStrokeWidth =
            this.resources.getInteger(integer.viewfinder_border_width)
        mDefaultBorderLineLength =
            this.resources.getInteger(integer.viewfinder_border_length)
        mViewFinderOffset = 0
        init()
    }

    constructor(
        context: Context?,
        attributeSet: AttributeSet?
    ) : super(context, attributeSet) {
        mDefaultLaserColor = this.resources.getColor(color.viewfinder_laser)
        mDefaultMaskColor = this.resources.getColor(color.viewfinder_mask)
        mDefaultBorderColor = this.resources.getColor(color.viewfinder_border)
        mDefaultBorderStrokeWidth =
            this.resources.getInteger(integer.viewfinder_border_width)
        mDefaultBorderLineLength =
            this.resources.getInteger(integer.viewfinder_border_length)
        mViewFinderOffset = 0
        init()
    }

    private fun init() {
        mLaserPaint = Paint()
        mLaserPaint!!.color = mDefaultLaserColor
        mLaserPaint!!.style = Paint.Style.FILL
        mFinderMaskPaint = Paint()
        mFinderMaskPaint!!.color = mDefaultMaskColor
        mBorderPaint = Paint()
        mBorderPaint!!.color = mDefaultBorderColor
        mBorderPaint!!.style = Paint.Style.STROKE
        mBorderPaint!!.strokeWidth = mDefaultBorderStrokeWidth.toFloat()
        mBorderPaint!!.isAntiAlias = true
        mBorderLineLength = mDefaultBorderLineLength
    }

    override fun setLaserColor(laserColor: Int) {
        mLaserPaint!!.color = laserColor
    }

    override fun setMaskColor(maskColor: Int) {
        mFinderMaskPaint!!.color = maskColor
    }

    override fun setBorderColor(borderColor: Int) {
        mBorderPaint!!.color = borderColor
    }

    override fun setBorderStrokeWidth(borderStrokeWidth: Int) {
        mBorderPaint!!.strokeWidth = borderStrokeWidth.toFloat()
    }

    override fun setBorderLineLength(borderLineLength: Int) {
        mBorderLineLength = borderLineLength
    }

    override fun setLaserEnabled(isLaserEnabled: Boolean) {
        mIsLaserEnabled = isLaserEnabled
    }

    override fun setBorderCornerRounded(isBorderCornersRounded: Boolean) {
        if (isBorderCornersRounded) {
            mBorderPaint!!.strokeJoin = Join.ROUND
        } else {
            mBorderPaint!!.strokeJoin = Join.BEVEL
        }
    }

    override fun setBorderAlpha(alpha: Float) {
        val colorAlpha = (255.0f * alpha).toInt()
        mBordersAlpha = alpha
        mBorderPaint!!.alpha = colorAlpha
    }

    override fun setBorderCornerRadius(borderCornersRadius: Int) {
        mBorderPaint!!.pathEffect = CornerPathEffect(borderCornersRadius.toFloat())
    }

    override fun setViewFinderOffset(offset: Int) {
        mViewFinderOffset = offset
    }

    override fun setSquareViewFinder(set: Boolean) {
        mSquareViewFinder = set
    }

    override fun setupViewFinder() {
        updateFramingRect()
        this.invalidate()
    }

    override fun getFramingRect(): Rect {
        return mFramingRect!!
    }

    public override fun onDraw(canvas: Canvas) {
        if (this.framingRect != null) {
            drawViewFinderMask(canvas)
            drawViewFinderBorder(canvas)
            if (mIsLaserEnabled) {
                drawLaser(canvas)
            }
        }
    }

    fun drawViewFinderMask(canvas: Canvas) {
        val width = canvas.width
        val height = canvas.height
        val framingRect = this.framingRect
        canvas.drawRect(
            0.0f,
            0.0f,
            width.toFloat(),
            framingRect.top.toFloat(),
            mFinderMaskPaint!!
        )
        canvas.drawRect(
            0.0f,
            framingRect.top.toFloat(),
            framingRect.left.toFloat(),
            (framingRect.bottom + 1).toFloat(),
            mFinderMaskPaint!!
        )
        canvas.drawRect(
            (framingRect.right + 1).toFloat(),
            framingRect.top.toFloat(),
            width.toFloat(),
            (framingRect.bottom + 1).toFloat(),
            mFinderMaskPaint!!
        )
        canvas.drawRect(
            0.0f,
            (framingRect.bottom + 1).toFloat(),
            width.toFloat(),
            height.toFloat(),
            mFinderMaskPaint!!
        )
    }

    fun drawViewFinderBorder(canvas: Canvas) {
        val framingRect = this.framingRect
        val rainbow:IntArray = intArrayOf(context.getColor(R.color.colorPrimaryDark), context.getColor(R.color.colorPrimary))
        val shader: Shader = LinearGradient(
            0F, 0f, 0f, width.toFloat(), rainbow,
            null, Shader.TileMode.MIRROR
        )
        val matrix = Matrix()
        matrix.setRotate(90F)
        shader.setLocalMatrix(matrix)
        mBorderPaint!!.shader = shader

        val path = Path()
        println("Framing rect: ${framingRect}")
        path.moveTo(
            framingRect.left.toFloat(),
            (framingRect.top + mBorderLineLength).toFloat()
        )
        path.lineTo(framingRect.left.toFloat(), framingRect.top.toFloat())
        path.lineTo(
            (framingRect.left + mBorderLineLength).toFloat(),
            framingRect.top.toFloat()
        )
        canvas.drawPath(path, mBorderPaint!!)
        path.moveTo(
            framingRect.right.toFloat(),
            (framingRect.top + mBorderLineLength).toFloat()
        )
        path.lineTo(framingRect.right.toFloat(), framingRect.top.toFloat())
        path.lineTo(
            (framingRect.right - mBorderLineLength).toFloat(),
            framingRect.top.toFloat()
        )


        canvas.drawPath(path, mBorderPaint!!)
        path.moveTo(
            framingRect.right.toFloat(),
            (framingRect.bottom - mBorderLineLength).toFloat()
        )
        path.lineTo(framingRect.right.toFloat(), framingRect.bottom.toFloat())
        path.lineTo(
            (framingRect.right - mBorderLineLength).toFloat(),
            framingRect.bottom.toFloat()
        )
        canvas.drawPath(path, mBorderPaint!!)
        path.moveTo(
            framingRect.left.toFloat(),
            (framingRect.bottom - mBorderLineLength).toFloat()
        )
        path.lineTo(framingRect.left.toFloat(), framingRect.bottom.toFloat())
        path.lineTo(
            (framingRect.left + mBorderLineLength).toFloat(),
            framingRect.bottom.toFloat()
        )
        canvas.drawPath(path, mBorderPaint!!)
    }

    fun drawLaser(canvas: Canvas) {
        val framingRect = this.framingRect
        mLaserPaint!!.alpha = SCANNER_ALPHA[scannerAlpha]
        scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.size
        val middle = framingRect.height() / 2 + framingRect.top
        canvas.drawRect(
            (framingRect.left + 2).toFloat(),
            (middle - 1).toFloat(),
            (framingRect.right - 1).toFloat(),
            (middle + 2).toFloat(),
            mLaserPaint!!
        )
        this.postInvalidateDelayed(
            80L,
            framingRect.left - 10,
            framingRect.top - 10,
            framingRect.right + 10,
            framingRect.bottom + 10
        )
    }

    override fun onSizeChanged(xNew: Int, yNew: Int, xOld: Int, yOld: Int) {
        updateFramingRect()
    }

    @Synchronized
    fun updateFramingRect() {
        val viewResolution =
            Point(this.width, this.height)
        val orientation = DisplayUtils.getScreenOrientation(this.context)
        var width: Int
        var height: Int
        val heightOffset = 0.9f
        if (mSquareViewFinder) {
            if (orientation != 1) {
                height = (this.height.toFloat() * heightOffset).toInt()
                width = height
            } else {
                width = (this.width.toFloat() * 0.625f).toInt()
                height = width
            }
        } else if (orientation != 1) {
            height = (this.height.toFloat() * heightOffset).toInt()
            width = (1.4f * height.toFloat()).toInt()
        } else {
            width = (this.width.toFloat() * 0.75f).toInt()
            height = (heightOffset * width.toFloat()).toInt()
        }
        if (width > this.width) {
            width = this.width - 50
        }
        if (height > this.height) {
            height = this.height - 50
        }
        val leftOffset = (viewResolution.x - width) / 2
        val topOffset = (viewResolution.y - height) / 3
        mFramingRect = Rect(
            leftOffset + mViewFinderOffset,
            topOffset + mViewFinderOffset,
            leftOffset + width - mViewFinderOffset,
            topOffset + height - mViewFinderOffset
        )
    }

    companion object {
        private const val TAG = "ViewFinderView"
        private const val PORTRAIT_WIDTH_RATIO = 0.75f
        private const val PORTRAIT_WIDTH_HEIGHT_RATIO = 1f
        private const val LANDSCAPE_HEIGHT_RATIO = 0.625f
        private const val LANDSCAPE_WIDTH_HEIGHT_RATIO = 1.4f
        private const val MIN_DIMENSION_DIFF = 50
        private const val DEFAULT_SQUARE_DIMENSION_RATIO = 0.625f
        private val SCANNER_ALPHA = intArrayOf(0, 64, 128, 192, 255, 192, 128, 64)
        private const val POINT_SIZE = 10
        private const val ANIMATION_DELAY = 80L
    }
}