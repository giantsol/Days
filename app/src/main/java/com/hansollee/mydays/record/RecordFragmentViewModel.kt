package com.hansollee.mydays.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hansollee.mydays.models.Record
import com.hansollee.mydays.toast
import org.threeten.bp.LocalDate

/**
 * Created by kevin-ee on 2019-01-31.
 */

class RecordFragmentViewModel: ViewModel() {
    private lateinit var currentDateLiveData: MutableLiveData<LocalDate>

    fun getCurrentDateLiveData(): LiveData<LocalDate> {
        if (!::currentDateLiveData.isInitialized) {
            currentDateLiveData = MutableLiveData()

            // 현재 Date가 초기값
            currentDateLiveData.value = LocalDate.now()
        }

        return currentDateLiveData
    }

    // currentDate를 days만큼 앞/뒤로 이동시킨다
    fun changeCurrentDate(days: Int) {
        val oneDayBefore = getCurrentDateLiveData().value.plusDays(days.toLong())
        currentDateLiveData.value = oneDayBefore
    }

    fun resetCurrentDateToToday() {
        currentDateLiveData.value = LocalDate.now()
    }

    fun commitRecord(isNew: Boolean, record: Record) {
        toast(record.toString())
    }

}