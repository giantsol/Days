package com.hansollee.mydays.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * Created by kevin-ee on 2019-02-11.
 */

class HeightMatchAspectRatioImageView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AppCompatImageView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val drawable = this.drawable
        if (drawable != null) {
            val height = MeasureSpec.getSize(heightMeasureSpec)
            val width = height * drawable.intrinsicWidth / drawable.intrinsicHeight
            super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), heightMeasureSpec)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

}
