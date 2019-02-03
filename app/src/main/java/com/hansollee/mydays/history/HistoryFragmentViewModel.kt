package com.hansollee.mydays.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hansollee.mydays.db.AppDatabase
import com.hansollee.mydays.db.TaskDao
import com.hansollee.mydays.models.History
import com.hansollee.mydays.models.Task
import com.hansollee.mydays.toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDate

/**
 * Created by kevin-ee on 2019-02-02.
 */

class HistoryFragmentViewModel: ViewModel() {
    private val taskDao: TaskDao = AppDatabase.getInstance().taskDao()

    private lateinit var allHistoryItemsLiveData: MutableLiveData<List<History>>
    private val allHistoryItems: ArrayList<History> = ArrayList()

    private var pagingEndDate = LocalDate.now()
    private val pagingValue = 10L

    private var getHistoryItemsDisposable: Disposable? = null

    private val isLoading: Boolean
        get() = getHistoryItemsDisposable?.isDisposed == false

    fun getAllHistoryItems(): LiveData<List<History>> {
        if (!::allHistoryItemsLiveData.isInitialized) {
            allHistoryItemsLiveData = MutableLiveData()
            loadNextHistoryItems(pagingEndDate.minusDays(pagingValue), pagingEndDate)
        }
        return allHistoryItemsLiveData
    }

    // startDate ~ endDate 사이에 있는 Task들을 가져와서 allHistoryItems에 append시킴
    private fun loadNextHistoryItems(startDate: LocalDate, endDate: LocalDate) {
        if (isLoading) {
            return
        }

        getHistoryItemsDisposable = taskDao.getTasksBetweenDates(startDate, endDate)
            .map { tasks ->
                tasks
                    .groupBy { it.date }
                    .map { History(it.key, it.value.sortedWith(compareBy(Task::fromTime, Task::toTime))) }
                    .sortedByDescending { it.date }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ histories ->
                allHistoryItems.addAll(histories)
                pagingEndDate = startDate.minusDays(1L)
                allHistoryItemsLiveData.value = allHistoryItems
            }, { error ->
                toast(error.message)
            })
    }

    override fun onCleared() {
        getHistoryItemsDisposable?.dispose()
    }
}
