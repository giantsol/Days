package com.hansollee.mydays.widgets

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.NumberPicker
import com.hansollee.mydays.R
import org.threeten.bp.LocalTime
import java.util.Locale

/**
 * Created by kevin-ee on 2019-02-01.
 */

// Android의 Timepicker는 width와 height를 변경해도 각각의 picker 크기가 안변해서 따로 만듦.
// 그 외의 특별한 기능은 없음.
class SimpleTimePicker
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr) {

    private class TwoDigitFormatter internal constructor() : NumberPicker.Formatter {
        internal val mBuilder = StringBuilder()

        internal var mZeroDigit: Char = ' '
        internal lateinit var mFmt: java.util.Formatter

        internal val mArgs = arrayOfNulls<Any>(1)

        init {
            val locale = Locale.getDefault()
            init(locale)
        }

        private fun init(locale: Locale) {
            mFmt = createFormatter(locale)
            mZeroDigit = getZeroDigit(locale)
        }

        override fun format(value: Int): String {
            val currentLocale = Locale.getDefault()
            if (mZeroDigit != getZeroDigit(currentLocale)) {
                init(currentLocale)
            }
            mArgs[0] = value
            mBuilder.delete(0, mBuilder.length)
            mFmt.format("%02d", *mArgs)
            return mFmt.toString()
        }

        private fun getZeroDigit(locale: Locale): Char {
            return '0'
        }

        private fun createFormatter(locale: Locale): java.util.Formatter {
            return java.util.Formatter(mBuilder, locale)
        }
    }

    companion object {
        private const val MIN_HOUR = 0
        private const val MAX_HOUR = 11

        private const val MIN_MINUTE = 0
        private const val MAX_MINUTE = 59

        private const val AM = 0
        private const val PM = 1
        private val amPmStrings = arrayOf("AM", "PM")

        private val twoDigitFormatter = TwoDigitFormatter()
    }

    private val inputMethodManager: InputMethodManager
        = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

    private val hourPicker: NumberPicker
    private val minutePicker: NumberPicker
    private val amPmPicker: NumberPicker

    private val hourIn24Format: Int
        get() = if (isAm()) hourPicker.value else hourPicker.value + 12

    val time: LocalTime
        get() = LocalTime.of(hourIn24Format, minutePicker.value)

    init {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        weightSum = 3f

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.simple_timepicker, this, true)

        hourPicker = view.findViewById(R.id.hour)
        minutePicker = view.findViewById(R.id.minute)
        amPmPicker = view.findViewById(R.id.amPm)

        hourPicker.also {
            it.minValue = MIN_HOUR
            it.maxValue = MAX_HOUR
            it.setFormatter(twoDigitFormatter)
            it.setOnValueChangedListener { picker, oldVal, newVal ->
                picker.requestFocus()
                hideKeyboardIfShown()

                if ((oldVal == MAX_HOUR && newVal == MIN_HOUR) ||
                    (oldVal == MIN_HOUR && newVal == MAX_HOUR)) {
                    invertAmPm()
                }
            }
        }

        minutePicker.also {
            it.minValue = MIN_MINUTE
            it.maxValue = MAX_MINUTE
            it.setFormatter(twoDigitFormatter)
            it.setOnValueChangedListener { picker, oldVal, newVal ->
                picker.requestFocus()
                hideKeyboardIfShown()

                if (oldVal == MAX_MINUTE && newVal == MIN_MINUTE) {
                    // 59분에서 00분으로 넘어감
                    val newHour = hourPicker.value + 1
                    if (newHour > MAX_HOUR) {
                        invertAmPm()
                    }
                    hourPicker.value = newHour
                } else if (oldVal == MIN_MINUTE && newVal == MAX_MINUTE) {
                    // 00분에서 59분으로 넘어감
                    val newHour = hourPicker.value - 1
                    if (newHour < MIN_HOUR) {
                        invertAmPm()
                    }
                    hourPicker.value = newHour
                }
            }
        }

        amPmPicker.also {
            it.minValue = AM
            it.maxValue = PM
            it.displayedValues = amPmStrings
            it.setOnValueChangedListener { picker, oldVal, newVal ->
                picker.requestFocus()
                hideKeyboardIfShown()
            }
        }
    }

    private fun invertAmPm() {
        amPmPicker.value = if (isAm()) PM else AM
    }

    private fun isAm(): Boolean = amPmPicker.value == AM

    private fun hideKeyboardIfShown() {
        if (inputMethodManager.isActive) {
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        }
    }

    // 파라미터로 받은 time 값을 picker들에 보여줌
    fun showTime(time: LocalTime) {
        hourPicker.value = time.hour % 12
        minutePicker.value = time.minute
        amPmPicker.value = if (time.hour < 12) AM else PM
    }
}