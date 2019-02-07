package com.hansollee.mydays.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hansollee.mydays.db.AppDatabase
import com.hansollee.mydays.db.TaskDao
import com.hansollee.mydays.models.Task
import com.hansollee.mydays.models.TaskDescription
import com.hansollee.mydays.today
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDate

/**
 * Created by kevin-ee on 2019-01-31.
 */

class TaskFragmentViewModel: ViewModel() {

    data class TasksResult(val tasks: List<Task>, val byUpdate: Boolean)

    private val currentDateLiveData: MutableLiveData<LocalDate> = MutableLiveData()
    private lateinit var currentTasksLiveData: MutableLiveData<TasksResult>
    private val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private lateinit var allTaskDescriptions: MutableLiveData<List<TaskDescription>>

    private var getTasksDisposable: Disposable? = null
    private var allTaskDescriptionsDisposable: Disposable? = null

    private val taskDao: TaskDao = AppDatabase.getInstance().taskDao()

    private var updatingDate: LocalDate? = null

    init {
        currentDateLiveData.value = today
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
        currentDateLiveData.value = today
    }

    fun insertNewTask(task: Task) {
        taskDao.insertTask(task).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                isLoadingLiveData.value = true
                updatingDate = task.date
            }
    }

    fun updateTask(task: Task) {
        taskDao.updateTask(task).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                isLoadingLiveData.value = true
                updatingDate = task.date
            }
    }

    fun deleteTask(task: Task) {
        taskDao.deleteTask(task).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                isLoadingLiveData.value = true
                updatingDate = task.date
            }
    }

    fun getCurrentTasks(): LiveData<TasksResult> {
        if (!::currentTasksLiveData.isInitialized) {
            currentTasksLiveData = MutableLiveData()
            loadTasksForDate(getCurrentDate().value)
        }

        return currentTasksLiveData
    }

    fun loadTasksForDate(date: LocalDate) {
        getTasksDisposable?.dispose()
        getTasksDisposable = taskDao.getTasksByDate(date)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _ ->
                isLoadingLiveData.value = true
            }
            .doFinally {
                isLoadingLiveData.value = false
            }
            .subscribe { tasks ->
                currentTasksLiveData.value = TasksResult(tasks, updatingDate == date)
                isLoadingLiveData.value = false
                updatingDate = null
            }
    }

    fun getLoadingStatus(): LiveData<Boolean> {
        return isLoadingLiveData
    }

    fun getAllTaskDescriptions(): LiveData<List<TaskDescription>> {
        if (!::allTaskDescriptions.isInitialized) {
            allTaskDescriptions = MutableLiveData()
            loadAllTasks()
        }

        return allTaskDescriptions
    }

    private fun loadAllTasks() {
        allTaskDescriptionsDisposable?.dispose()
        allTaskDescriptionsDisposable = taskDao.getAllTaskDescriptions()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ tasks ->
                allTaskDescriptions.value = tasks
            })
    }

    override fun onCleared() {
        getTasksDisposable?.dispose()
        allTaskDescriptionsDisposable?.dispose()
    }
}