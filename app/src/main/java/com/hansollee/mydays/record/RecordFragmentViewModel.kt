package com.hansollee.mydays.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hansollee.mydays.getDateAfter
import com.hansollee.mydays.getDateBefore
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

    fun moveOneDayBefore() {
        val oneDayBefore = getCurrentDateLiveData().value.getDateBefore(1)
        currentDateLiveData.value = oneDayBefore
    }

    fun moveOneDayAfter() {
        val oneDayAfter = getCurrentDateLiveData().value.getDateAfter(1)
        currentDateLiveData.value = oneDayAfter
    }

    fun resetCurrentDateToToday() {
        currentDateLiveData.value = Date()
    }

    override fun onCleared() {
        super.onCleared()
    }

}