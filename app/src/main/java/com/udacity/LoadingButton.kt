package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.content.withStyledAttributes
import kotlinx.android.synthetic.main.content_main.view.*
import kotlin.math.min
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var valueAnimator = ValueAnimator()
    private var circleAnimator = ValueAnimator()

    private var width = 0f
    private var sweepAngle = 0f
    private var buttonText: String
    private var playAnimation = false

    private var circleRadius = 0f

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                playAnimation = true
                buttonText = "We are loading"
                invalidate()
                setLoadingState(ButtonState.Loading)
            }
            ButtonState.Loading -> {
                playProgressAnim()
            }
            ButtonState.Completed -> {
                playAnimation = false
                buttonText = "Download"
                paint.color = buttonColor
                invalidate()
            }
        }
    }

    private fun playProgressAnim() {
        valueAnimator = ValueAnimator.ofFloat(0F, measuredWidth.toFloat()).apply {
            duration = 2000
            addUpdateListener { valueAnimator ->
                width = valueAnimator.animatedValue as Float
                valueAnimator.repeatCount = ValueAnimator.INFINITE
                valueAnimator.repeatMode = ValueAnimator.REVERSE
                valueAnimator.interpolator = AccelerateInterpolator()
                invalidate()
            }
            start()
        }
        circleAnimator = ValueAnimator.ofFloat(0f, 360f)
                .apply {
                    duration = 2000
                    addUpdateListener { animator ->
                        sweepAngle = animator.animatedValue as Float
                        animator.interpolator = AccelerateInterpolator()
                        invalidate()
                    }
                    start()
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator?) {
                            custom_button.isEnabled = false
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            custom_button.isEnabled = true
                            setLoadingState(ButtonState.Completed)
                        }
                    })
                }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 55.0f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private var buttonColor = 0
    private var buttonColor2 = 0
    private var textColor = 0

    init {
        buttonText = "Download"
        context.withStyledAttributes(attrs,R.styleable.LoadingButton){
            buttonColor = getColor(R.styleable.LoadingButton_buttonColor,0)
            buttonColor2 = getColor(R.styleable.LoadingButton_buttonColor2,0)
            textColor = getColor(R.styleable.LoadingButton_textColor,0)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        circleRadius = (min(w, h) / 2 * 0.5).toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = buttonColor
        canvas!!.drawColor(paint.color)
        canvas.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paint)
        paint.color = textColor
        paint.textSize = 50.0f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(buttonText, widthSize.toFloat() / 2, heightSize.toFloat() / 2, paint)
        if (playAnimation) {
            paint.color = buttonColor2
            canvas.drawRect(0f, 0f, width, measuredHeight.toFloat(), paint)
            paint.color = textColor
            canvas.drawText(buttonText, widthSize.toFloat() / 2, heightSize.toFloat() / 2, paint)
            paint.color = Color.YELLOW
            canvas.drawArc(
                    (widthSize - 90f),
                    (heightSize / 2) - 25f,
                    (widthSize - 50f),
                    (heightSize / 2) + 25f,
                    0f, sweepAngle, true, paint
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
                MeasureSpec.getSize(w),
                heightMeasureSpec,
                0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun setLoadingState(state: ButtonState) {
        buttonState = state
    }

}