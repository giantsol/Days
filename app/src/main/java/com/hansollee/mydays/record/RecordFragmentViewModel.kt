package com.hansollee.mydays.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hansollee.mydays.db.AppDatabase
import com.hansollee.mydays.db.RecordDao
import com.hansollee.mydays.models.Record
import com.hansollee.mydays.toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDate

/**
 * Created by kevin-ee on 2019-01-31.
 */

class RecordFragmentViewModel: ViewModel() {
    private lateinit var currentDateLiveData: MutableLiveData<LocalDate>
    private lateinit var recordsLiveData: MutableLiveData<List<Record>>

    private var getRecordsDisposable: Disposable? = null

    private val recordDao: RecordDao = AppDatabase.getInstance().recordDao()

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
        recordDao.insertRecord(record).subscribeOn(Schedulers.io()).subscribe()
    }

    fun getRecordsLiveData(): LiveData<List<Record>> {
        if (!::recordsLiveData.isInitialized) {
            recordsLiveData = MutableLiveData()
            loadRecordsForDate(getCurrentDateLiveData().value)
        }

        return recordsLiveData
    }

    fun loadRecordsForDate(date: LocalDate) {
        getRecordsDisposable?.dispose()
        getRecordsDisposable = recordDao.getRecordsByDate(date)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { records -> recordsLiveData.value = records }
    }

    override fun onCleared() {
        getRecordsDisposable?.dispose()
    }
}