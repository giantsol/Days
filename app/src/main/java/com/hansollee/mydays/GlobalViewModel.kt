package com.hansollee.mydays

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import org.threeten.bp.LocalDate

/**
 * Created by kevin-ee on 2019-02-09.
 */

class GlobalViewModel: ViewModel() {

    companion object {
        fun getInstance(activity: FragmentActivity): GlobalViewModel
            = ViewModelProviders.of(activity).get(GlobalViewModel::class.java)
    }

    private val today: MutableLiveData<LocalDate> = MutableLiveData()

    init {
        today.value = LocalDate.now()
    }

    fun getToday(): LiveData<LocalDate> {
        return today
    }

    fun getTodayValue(): LocalDate = today.value

    fun updateToday() {
        today.value = LocalDate.now()
    }
}