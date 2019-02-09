package com.hansollee.mydays.tasks

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.hansollee.mydays.db.AppDatabase
import com.hansollee.mydays.db.TaskDao
import com.hansollee.mydays.models.Task
import com.hansollee.mydays.models.TaskPickerItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDate

/**
 * Created by kevin-ee on 2019-01-31.
 */

class TasksViewModel(private var today: LocalDate): ViewModel() {

    companion object {

        fun getInstance(activity: FragmentActivity, today: LocalDate): TasksViewModel
            = ViewModelProviders.of(activity, TasksViewModelFactory(today)).get(TasksViewModel::class.java)

    }

    data class TasksResult(val tasks: List<Task>, val byUserUpdate: Boolean)

    private val currentDate: MutableLiveData<LocalDate> = MutableLiveData()
    private lateinit var currentTasks: MutableLiveData<TasksResult>
    private val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    private lateinit var allTaskPickerItems: MutableLiveData<List<TaskPickerItem>>

    private var loadCurrentTasksWork: Disposable? = null
    private var loadAllTaskPickerItemsWork: Disposable? = null

    private val taskDao: TaskDao = AppDatabase.getInstance().taskDao()

    // 사용자가 update/insert/delete 등을 한 date 값
    private var dateThatUserModified: LocalDate? = null

    init {
        currentDate.value = today
        isLoading.value = true
    }

    // 사용자가 선택한 날짜
    fun getCurrentDate(): LiveData<LocalDate> {
        return currentDate
    }

    fun getCurrentDateValue(): LocalDate = currentDate.value

    // currentDate를 days만큼 앞/뒤로 이동시킨다
    fun changeCurrentDate(days: Int) {
        currentDate.value = getCurrentDateValue().plusDays(days.toLong())
    }

    fun resetCurrentDateToToday() {
        setCurrentDate(today)
    }

    fun setCurrentDate(date: LocalDate) {
        if (currentDate.value != date) {
            currentDate.value = date
        }
    }

    fun insertNewTask(task: Task) {
        taskDao.insertTask(task).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                isLoading.value = true
                //TODO: 이거 어케할거냐
//                dateThatUserModified = task.date
            }
    }

    fun updateTask(task: Task) {
        taskDao.updateTask(task).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                isLoading.value = true
//                dateThatUserModified = task.date
            }
    }

    fun deleteTask(task: Task) {
        taskDao.deleteTask(task).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                isLoading.value = true
//                dateThatUserModified = task.date
            }
    }

    fun getCurrentTasks(): LiveData<TasksResult> {
        if (!::currentTasks.isInitialized) {
            currentTasks = MutableLiveData()
            loadTasksForDate(getCurrentDateValue())
        }

        return currentTasks
    }

    fun loadTasksForDate(date: LocalDate) {
        loadCurrentTasksWork?.dispose()
        loadCurrentTasksWork = taskDao.getTasksByDate(date)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _ ->
                isLoading.value = true
            }
            .doFinally {
                isLoading.value = false
            }
            .subscribe { tasks ->
                currentTasks.value = TasksResult(tasks, dateThatUserModified == date)
                isLoading.value = false
                dateThatUserModified = null
            }
    }

    fun getLoadingStatus(): LiveData<Boolean> {
        return isLoading
    }

    fun getAllTaskPickerItems(): LiveData<List<TaskPickerItem>> {
        if (!::allTaskPickerItems.isInitialized) {
            allTaskPickerItems = MutableLiveData()
            loadAllTaskPickerItems()
        }

        return allTaskPickerItems
    }

    private fun loadAllTaskPickerItems() {
        loadAllTaskPickerItemsWork?.dispose()
        loadAllTaskPickerItemsWork = taskDao.getAllTaskPickerItems()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ tasks ->
                allTaskPickerItems.value = tasks
            })
    }

    fun updateTodayValue(today: LocalDate): Boolean {
        return if (this.today != today) {
            this.today = today
            true
        } else {
            false
        }
    }

    override fun onCleared() {
        loadCurrentTasksWork?.dispose()
        loadAllTaskPickerItemsWork?.dispose()
    }
}