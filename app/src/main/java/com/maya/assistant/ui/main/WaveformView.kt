package com.maya.assistant.ui.main

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class WaveformView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val BAR_COUNT = 20
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF1744")
        style = Paint.Style.FILL
    }

    private val barHeights = FloatArray(BAR_COUNT) { 0.1f }
    private val targetHeights = FloatArray(BAR_COUNT) { 0.1f }
    private var isAnimating = false
    private var animator: ValueAnimator? = null

    fun startAnimation() {
        if (isAnimating) return
        isAnimating = true
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 50
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                for (i in 0 until BAR_COUNT) {
                    barHeights[i] += (targetHeights[i] - barHeights[i]) * 0.3f
                }
                invalidate()
            }
            start()
        }
    }

    fun stopAnimation() {
        isAnimating = false
        animator?.cancel()
        animator = null
        for (i in 0 until BAR_COUNT) {
            targetHeights[i] = 0.1f
            barHeights[i] = 0.1f
        }
        invalidate()
    }

    fun setAmplitude(rms: Float) {
        for (i in 0 until BAR_COUNT) {
            val noise = (Math.random() * 0.3 - 0.15).toFloat()
            targetHeights[i] = (rms + noise).coerceIn(0.05f, 1f)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val barWidth = width.toFloat() / BAR_COUNT * 0.7f
        val gap = width.toFloat() / BAR_COUNT * 0.3f
        val maxHeight = height.toFloat()

        for (i in 0 until BAR_COUNT) {
            val barHeight = barHeights[i] * maxHeight
            val left = i * (barWidth + gap) + gap / 2
            val top = (height - barHeight) / 2
            val right = left + barWidth
            val bottom = top + barHeight

            val alpha = (150 + barHeights[i] * 105).toInt().coerceIn(150, 255)
            paint.alpha = alpha
            canvas.drawRect(left, top, right, bottom, paint)
        }
        paint.alpha = 255
    }
}
