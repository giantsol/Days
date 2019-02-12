package com.hansollee.mydays.widgets

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.hansollee.mydays.R

/**
 * Created by kevin-ee on 2019-02-11.
 */
class CategoryView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr) {

    private val thumbnail: ImageView
    private val taskDescription: TextView
    private val topMargin: Int

    init {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        topMargin =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, context.resources.displayMetrics).toInt()

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.view_history_category_view, this, true)

        thumbnail = view.findViewById(R.id.thumbnail)
        taskDescription = view.findViewById(R.id.task_description)
    }

    fun update(thumbnailColor: Int, description: String, durationString: String) {
        (thumbnail.drawable.mutate() as ColorDrawable).color = thumbnailColor
        taskDescription.text = "$description ($durationString)"
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val lp = layoutParams as MarginLayoutParams
        if (lp.topMargin != topMargin) {
            lp.topMargin = topMargin
        }
    }
}
