package com.hansollee.mydays.history

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
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

class HistoryViewModel(private var today: LocalDate): ViewModel() {

    companion object {

        fun getInstance(activity: FragmentActivity, today: LocalDate): HistoryViewModel
            = ViewModelProviders.of(activity, HistoryViewModelFactory(today)).get(HistoryViewModel::class.java)

    }

    private val taskDao: TaskDao = AppDatabase.getInstance().taskDao()

    private lateinit var allHistory: MutableLiveData<List<History>>
    private val allHistoryInternal: ArrayList<History> = ArrayList()

    private var pagingEndDate = today
    // 한번 새로 요청할때마다 몇개씩 요청할건지
    private val pagingValue = 10L

    private var historyLoadingWork: Disposable? = null

    private val isLoading: Boolean
        get() = historyLoadingWork?.isDisposed == false

    fun getAllHistory(): LiveData<List<History>> {
        if (!::allHistory.isInitialized) {
            allHistory = MutableLiveData()
            loadNextHistory()
        }
        return allHistory
    }

    // startDate ~ endDate 사이에 있는 Task들을 가져와서 allHistory에 append시킴
    private fun loadNextHistory() {
        if (isLoading) {
            return
        }

        val startDate = pagingEndDate.minusDays(pagingValue)
        val endDate = pagingEndDate

        historyLoadingWork = taskDao.getTasksBetweenDates(startDate, endDate)
            .map { tasks ->
                tasks
                    .groupBy { it.date }
                    .map { History(it.key, it.value.sortedWith(compareBy(Task::startTime, Task::endTime))) }
                    .sortedByDescending { it.date }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ histories ->
                if (histories.isEmpty()) {
                    // TODO: 더이상 남아있는 히스토리가 없다고 알려줘야함
                } else {
                    allHistoryInternal.addAll(histories)
                    allHistory.value = allHistoryInternal

                    pagingEndDate = histories.last().date.minusDays(1L)
                }
            }, { error ->
                toast(error.message)
            })
    }

    // 유저가 추가/수정을 통해 특정 날짜의 tasks들에 변화를 가했을 때,
    // db를 다시 찔러서 업데이트 하는 대신 수동적으로 업데이트 하도록.
    // 기록이 엄청 많이 쌓여있을 경우, 계속 db찌르는건 부담될까봐
    fun onUserUpdatedTasks(date: LocalDate, tasks: List<Task>) {
        // History에는 오늘 이하로만 보여주도록
        if (date > today) {
            return
        }

        val updatedHistory = History(date, tasks)
        val indexToUpdate = allHistoryInternal.binarySearch(updatedHistory, compareByDescending(History::date))
        if (indexToUpdate >= 0 && indexToUpdate < allHistoryInternal.size) {
            if (updatedHistory.tasks.isEmpty()) {
                allHistoryInternal.removeAt(indexToUpdate)
            } else {
                allHistoryInternal[indexToUpdate] = updatedHistory
            }
            allHistory.value = allHistoryInternal
        } else if (updatedHistory.tasks.isNotEmpty()) {
            val indexToInsert = -indexToUpdate - 1
            if (indexToInsert >= 0 && indexToInsert <= allHistoryInternal.size) {
                allHistoryInternal.add(indexToInsert, updatedHistory)
                allHistory.value = allHistoryInternal
            }
        }
    }

    fun updateTodayValue(today: LocalDate): Boolean {
        return if (this.today != today) {
            this.today = today
            true
        } else {
            false
        }
    }

    fun reloadHistory() {
        pagingEndDate = today
        allHistoryInternal.clear()
        loadNextHistory()
    }

    override fun onCleared() {
        historyLoadingWork?.dispose()
    }
}
