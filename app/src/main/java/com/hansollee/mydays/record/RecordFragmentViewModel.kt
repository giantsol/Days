package com.hansollee.mydays.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hansollee.mydays.getChangedDate
import java.util.Date

/**
 * Created by kevin-ee on 2019-01-31.
 */

class RecordFragmentViewModel: ViewModel() {
    private lateinit var currentDateLiveData: MutableLiveData<Date>

    fun getCurrentDateLiveData(): LiveData<Date> {
        if (!::currentDateLiveData.isInitialized) {
            currentDateLiveData = MutableLiveData()

            // 현재 Date가 초기값
            currentDateLiveData.value = Date()
        }

        return currentDateLiveData
    }

    // currentDate를 days만큼 앞/뒤로 이동시킨다
    fun changeCurrentDate(days: Int) {
        val oneDayBefore = getCurrentDateLiveData().value.getChangedDate(days)
        currentDateLiveData.value = oneDayBefore
    }

    fun resetCurrentDateToToday() {
        currentDateLiveData.value = Date()
    }

    override fun onCleared() {
        super.onCleared()
    }

}