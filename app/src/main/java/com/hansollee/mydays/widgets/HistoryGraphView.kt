package com.hansollee.mydays.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.hansollee.mydays.R

/**
 * Created by kevin-ee on 2019-02-03.
 */

class HistoryGraphView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr) {

    private var defaultColor: Int = ContextCompat.getColor(context, R.color.default_history_graph_color)
    private val paint: Paint = Paint()
    private val graphRect: Rect = Rect()
    private val graphHeight: Int
    private val xAxisTextSize: Int

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.HistoryGraphView)

        graphHeight = a.getDimensionPixelSize(R.styleable.HistoryGraphView_graphHeight, 0)
        xAxisTextSize = a.getDimensionPixelSize(R.styleable.HistoryGraphView_xAxisTextSize, 0)

        a.recycle()
    }

    fun setDefaultColor(@ColorInt color: Int) {
        defaultColor = color
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
            graphHeight + xAxisTextSize)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        graphRect.set(0, 0, width, graphHeight)
    }

    override fun onDraw(canvas: Canvas) {
        paint.color = defaultColor
        canvas.drawRect(graphRect, paint)
    }
}
