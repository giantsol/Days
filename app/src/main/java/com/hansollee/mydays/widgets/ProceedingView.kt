package com.hansollee.mydays.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.hansollee.mydays.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * Created by kevin-ee on 2019-02-11.
 */

class ProceedingView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr) {

    private val icon1: ImageView
    private val icon2: ImageView
    private val icon3: ImageView

    private var animationWork: Disposable? = null
    private var currentDisplayedIconCount = 0

    init {
        orientation = LinearLayout.HORIZONTAL

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.proceeding_view, this, true)

        icon1 = view.findViewById(R.id.icon1)
        icon2 = view.findViewById(R.id.icon2)
        icon3 = view.findViewById(R.id.icon3)
    }

    override fun onVisibilityChanged(changedView: View?, visibility: Int) {
        if (visibility == View.VISIBLE) {
            animationWork = startAnimating()
        } else {
            animationWork?.dispose()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (visibility == View.VISIBLE) {
            animationWork = startAnimating()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        animationWork?.dispose()
    }

    private fun startAnimating(): Disposable {
        animationWork?.dispose()

        return Observable.interval(500L, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { _ ->
                currentDisplayedIconCount += 1
                if (currentDisplayedIconCount == 4) {
                    currentDisplayedIconCount = 0
                }

                icon1.visibility = if (currentDisplayedIconCount >= 1) View.VISIBLE else View.INVISIBLE
                icon2.visibility = if (currentDisplayedIconCount >= 2) View.VISIBLE else View.INVISIBLE
                icon3.visibility = if (currentDisplayedIconCount >= 3) View.VISIBLE else View.INVISIBLE
            }
    }

}
