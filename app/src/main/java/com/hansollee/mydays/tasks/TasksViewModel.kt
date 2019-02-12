package com.hansollee.mydays.tasks

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.hansollee.mydays.db.AppDatabase
import com.hansollee.mydays.db.TaskDao
import com.hansollee.mydays.models.Task
import com.hansollee.mydays.models.UniqueTask
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.threeten.bp.LocalDate

/**
 * Created by kevin-ee on 2019-01-31.
 */

class TasksViewModel(private var today: LocalDate): ViewModel() {

    companion object {

        fun getInstance(activity: FragmentActivity, today: LocalDate): TasksViewModel
            = ViewModelProviders.of(activity, TasksViewModelFactory(today)).get(TasksViewModel::class.java)

    }

    private val currentDate: MutableLiveData<LocalDate> = MutableLiveData()
    private lateinit var currentTasks: MutableLiveData<List<Task>>
    private val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    private lateinit var allUniqueTasks: MutableLiveData<List<UniqueTask>>
    private val dateUpdatedByUserEvent = PublishSubject.create<Pair<LocalDate, List<Task>>>()

    private var loadTasksWork: Disposable? = null
    private var loadAllUniqueTasksWork: Disposable? = null

    private val taskDao: TaskDao = AppDatabase.getInstance().taskDao()

    private var dirtyDatesByUserModify: ArrayList<LocalDate> = ArrayList()

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
                loadTask(task)
            }
    }

    fun updateTask(task: Task) {
        taskDao.updateTask(task).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                loadTask(task)
            }
    }

    fun deleteTask(task: Task) {
        taskDao.deleteTask(task).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                loadTask(task)
            }
    }

    private fun loadTask(task: Task) {
        val startDate = task.startDateTime.toLocalDate()
        val endDate = task.endDateTime?.toLocalDate()
        if (endDate == null) {
            dirtyDatesByUserModify.add(startDate)
        } else {
            var d = startDate
            while (d <= endDate) {
                dirtyDatesByUserModify.add(d)
                d = d.plusDays(1L)
            }
        }

        loadTasksForDate(dirtyDatesByUserModify)
    }

    // 유저가 선택한 currentDate에 속한 Tasks를 가져옴
    fun getCurrentTasks(): LiveData<List<Task>> {
        if (!::currentTasks.isInitialized) {
            currentTasks = MutableLiveData()
            loadTasksForDate(listOf(getCurrentDateValue()))
        }

        return currentTasks
    }

    fun loadTasksForDate(dates: List<LocalDate>) {
        loadTasksWork?.dispose()
        loadTasksWork = Observable.fromIterable(dates)
            .flatMapSingle { date ->
                taskDao.getTasksByDate(date).map { Pair(date, it) }.subscribeOn(Schedulers.io())
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _ ->
                isLoading.value = true
            }
            .doFinally {
                isLoading.value = false
            }
            .subscribe { pairOfDateAndTasks ->
                val date = pairOfDateAndTasks.first
                val tasks = pairOfDateAndTasks.second

                if (date == getCurrentDateValue()) {
                    currentTasks.value = tasks
                }

                if (date in dirtyDatesByUserModify) {
                    dispatchDateChangedByUserModify(date, tasks)
                    dirtyDatesByUserModify.remove(date)
                }
            }
    }

    private fun dispatchDateChangedByUserModify(date: LocalDate, tasks: List<Task>) {
        dateUpdatedByUserEvent.onNext(Pair(date, tasks))
    }

    fun observeDateUpdatedByUser(): Observable<Pair<LocalDate, List<Task>>> {
        return dateUpdatedByUserEvent
    }

    fun getLoadingStatus(): LiveData<Boolean> {
        return isLoading
    }

    fun getAllUniqueTasks(): LiveData<List<UniqueTask>> {
        if (!::allUniqueTasks.isInitialized) {
            allUniqueTasks = MutableLiveData()
            loadAllUniqueTasks()
        }

        return allUniqueTasks
    }

    private fun loadAllUniqueTasks() {
        loadAllUniqueTasksWork?.dispose()
        loadAllUniqueTasksWork = taskDao.getAllUniqueTasks()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ tasks ->
                allUniqueTasks.value = tasks
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
        loadTasksWork?.dispose()
        loadAllUniqueTasksWork?.dispose()
    }
}