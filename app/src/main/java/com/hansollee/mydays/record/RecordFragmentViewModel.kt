package com.hansollee.mydays.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hansollee.mydays.db.AppDatabase
import com.hansollee.mydays.db.RecordDao
import com.hansollee.mydays.models.Record
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDate

/**
 * Created by kevin-ee on 2019-01-31.
 */

class RecordFragmentViewModel: ViewModel() {
    private val currentDateLiveData: MutableLiveData<LocalDate> = MutableLiveData()
    private lateinit var recordsLiveData: MutableLiveData<List<Record>>
    private val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()

    private var getRecordsDisposable: Disposable? = null

    private val recordDao: RecordDao = AppDatabase.getInstance().recordDao()

    init {
        currentDateLiveData.value = LocalDate.now()
        isLoadingLiveData.value = true
    }

    fun getCurrentDate(): LiveData<LocalDate> {
        return currentDateLiveData
    }

    // currentDate를 days만큼 앞/뒤로 이동시킨다
    fun changeCurrentDate(days: Int) {
        val oneDayBefore = getCurrentDate().value.plusDays(days.toLong())
        currentDateLiveData.value = oneDayBefore
    }

    fun resetCurrentDateToToday() {
        currentDateLiveData.value = LocalDate.now()
    }

    fun insertNewRecord(record: Record) {
        recordDao.insertRecord(record).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                isLoadingLiveData.value = true
            }
    }

    fun updateRecord(record: Record) {
        recordDao.updateRecord(record).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                isLoadingLiveData.value = true
            }
    }

    fun deleteRecord(record: Record) {
        recordDao.deleteRecord(record).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                isLoadingLiveData.value = true
            }
    }

    fun getRecords(): LiveData<List<Record>> {
        if (!::recordsLiveData.isInitialized) {
            recordsLiveData = MutableLiveData()
            loadRecordsForDate(getCurrentDate().value)
        }

        return recordsLiveData
    }

    fun loadRecordsForDate(date: LocalDate) {
        getRecordsDisposable?.dispose()
        getRecordsDisposable = recordDao.getRecordsByDate(date)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _ ->
                isLoadingLiveData.value = true
            }
            .doFinally {
                isLoadingLiveData.value = false
            }
            .subscribe { records ->
                recordsLiveData.value = records
                isLoadingLiveData.value = false
            }
    }

    fun getLoadingStatus(): LiveData<Boolean> {
        return isLoadingLiveData
    }

    override fun onCleared() {
        getRecordsDisposable?.dispose()
    }
}