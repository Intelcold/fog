package com.example.fogsonar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class WaveformView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val paint = Paint()
    private var waveform: ShortArray? = null

    init {
        paint.color = Color.parseColor("#FFC107") // Amber color
        paint.strokeWidth = 2f
    }

    fun updateWaveform(waveform: ShortArray) {
        this.waveform = waveform
        invalidate() // Redraw the view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        waveform?.let {
            val width = width.toFloat()
            val height = height.toFloat()
            val centerY = height / 2

            val step = width / it.size

            for (i in 0 until it.size - 1) {
                val x1 = i * step
                val y1 = centerY + (it[i] / 32767f) * (height / 2)
                val x2 = (i + 1) * step
                val y2 = centerY + (it[i + 1] / 32767f) * (height / 2)
                canvas.drawLine(x1, y1, x2, y2, paint)
            }
        }
    }
}