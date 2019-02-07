package com.hansollee.mydays.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.hansollee.mydays.R
import com.hansollee.mydays.models.History
import com.hansollee.mydays.toMinuteOfDay

/**
 * Created by kevin-ee on 2019-02-03.
 */

class HistoryGraphView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr) {

    companion object {
        private const val MINUTES_PER_HOUR = 60f
    }

    private var defaultColor: Int = ContextCompat.getColor(context, R.color.default_history_graph_color)
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val graphRect: RectF = RectF()
    private val graphHeight: Float
    private val xAxisTextSize: Float
    private var historyData: History? = null
    private var distancePerMinute: Float = 0f
    private val hoursToWrite = arrayOf(0, 3, 6, 9, 12, 15, 18, 21)
    private val hoursToWriteXAxis = hoursToWrite.map { it * MINUTES_PER_HOUR }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.HistoryGraphView)

        graphHeight = a.getDimensionPixelSize(R.styleable.HistoryGraphView_graphHeight, 0).toFloat()
        xAxisTextSize = a.getDimensionPixelSize(R.styleable.HistoryGraphView_xAxisTextSize, 0).toFloat()

        a.recycle()
    }

    fun setDefaultColor(@ColorInt color: Int) {
        defaultColor = color
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
            (graphHeight + xAxisTextSize).toInt())
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        distancePerMinute = width / (24 * 60f)
    }

    override fun onDraw(canvas: Canvas) {
        paint.color = defaultColor
        graphRect.set(0f, 0f, width.toFloat(), graphHeight)
        canvas.drawRect(graphRect, paint)

        // draw x axis hours
        paint.color = Color.BLACK
        paint.textSize = xAxisTextSize
        val y = graphHeight + xAxisTextSize
        for (i in 0.until(hoursToWrite.size)) {
            canvas.drawText(hoursToWrite[i].toString(), hoursToWriteXAxis[i] * distancePerMinute, y, paint)
        }

        historyData?.also { history ->
            val tasks = history.tasks

            for (task in tasks) {
                paint.color = task.colorInt
                graphRect.left = task.startTime.toMinuteOfDay() * distancePerMinute
                graphRect.right = task.endTime.toMinuteOfDay() * distancePerMinute
                canvas.drawRect(graphRect, paint)
            }
        }
    }

    fun drawHistory(history: History) {
        historyData = history
        invalidate()
    }
}
