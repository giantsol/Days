package com.hansollee.mydays.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hansollee.mydays.db.AppDatabase
import com.hansollee.mydays.db.RecordDao
import com.hansollee.mydays.models.History
import com.hansollee.mydays.toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDate

/**
 * Created by kevin-ee on 2019-02-02.
 */

class HistoryFragmentViewModel: ViewModel() {
    val today: LocalDate = LocalDate.now()

    private val recordDao: RecordDao = AppDatabase.getInstance().recordDao()

    private lateinit var historyItemsLiveData: MutableLiveData<List<History>>
    private val historyItems: ArrayList<History> = ArrayList()

    private var pagingStartDate = today
    private val pagingValue = 10L

    private var getHistoriesDisposable: Disposable? = null

    private val isLoading: Boolean
        get() = getHistoriesDisposable?.isDisposed == false

    fun getHistoryItems(): LiveData<List<History>> {
        if (!::historyItemsLiveData.isInitialized) {
            historyItemsLiveData = MutableLiveData()
            loadHistoryItems(pagingStartDate.minusDays(pagingValue), pagingStartDate)
        }
        return historyItemsLiveData
    }

    private fun loadHistoryItems(startDate: LocalDate, endDate: LocalDate) {
        if (isLoading) {
            return
        }

        getHistoriesDisposable = recordDao.getRecordsBetweenDates(startDate, endDate)
            .map { records -> records.groupBy { it.date }.map { History(it.key, it.value) } }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ histories ->
                historyItems.addAll(histories)
                pagingStartDate = endDate
                historyItemsLiveData.value = historyItems
            }, { error ->
                toast(error.message)
            })
    }

    override fun onCleared() {
        getHistoriesDisposable?.dispose()
    }
}
