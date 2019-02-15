package com.hansollee.mydays

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.ResultReceiver
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.hansollee.mydays.db.AppDatabase
import com.hansollee.mydays.db.TaskDao
import com.hansollee.mydays.models.Task
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import java.util.concurrent.TimeUnit

/**
 * Created by kevin-ee on 2019-02-08.
 */

class MiniDaysService: Service() {
    companion object {
        const val ACTION_START_SERVICE = "action.start.service"
        const val ACTION_STOP_SERVICE = "action.stop.service"
        const val ACTION_START_TASK = "action.start.task"
        const val ACTION_STOP_TASK = "action.stop.task"

        private const val NOTIFICATION_CHANNEL_ID = "mini.days.channel"
        private const val NOTIFICATION_CHANNEL_NAME = "MiniDays"
        private const val SERVICE_ID = 1001
    }

    private var currentTaskDescription = ""
    private var currentColor: Int = 0
    private var blackColor: Int = 0
    private var greyColor: Int = 0
    private lateinit var emptyTaskDescriptionComment: String
    private var timeTicker: Disposable? = null
    private lateinit var startText: String
    private lateinit var stopText: String
    private var currentElapsedTime: LocalTime? = null
    private var startDateTime: LocalDateTime? = null
    private var startedTask: Task? = null

    private val taskDao: TaskDao = AppDatabase.getInstance().taskDao()

    private val simpleTaskEditResultReceiver = object: ResultReceiver(null) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
            if (resultCode == Activity.RESULT_OK && resultData != null) {
                currentTaskDescription = resultData.getString(SimpleTaskEditDialogActivity.KEY_TASK_DESCRIPTION)
                currentColor = resultData.getInt(SimpleTaskEditDialogActivity.KEY_COLOR)
                updateNotification()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW).also {
                it.description = "Notification channel for MiniDays"
                it.enableLights(false)
                it.enableVibration(false)
                it.setShowBadge(false)
                it.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }
            notificationManager.createNotificationChannel(channel)
        }

        currentColor = ContextCompat.getColor(this, R.color.default_task_thumbnail_color)
        blackColor = ContextCompat.getColor(this, android.R.color.black)
        greyColor = ContextCompat.getColor(this, R.color.grey)
        emptyTaskDescriptionComment = resources.getString(R.string.noti_empty_task_description)
        startText = resources.getString(R.string.start_text)
        stopText = resources.getString(R.string.stop_text)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            when (intent.action) {
                ACTION_START_SERVICE -> {
                    updateNotification()
                }
                ACTION_STOP_SERVICE -> {
                    stopForeground(true)
                    stopSelf()
                }
                ACTION_START_TASK -> {
                    startTask()
                }
                ACTION_STOP_TASK -> {
                    stopTask()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun updateNotification() {
        val view = getCurrentNotificationView()

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(view)
            .setContentIntent(getSimpleTaskEditActivityIntent())
            .build()

        startForeground(SERVICE_ID, notification)
    }

    private fun startTask() {
        if (isTaskStarted()) {
            return
        }

        if (currentTaskDescription.isEmpty()) {
            toast(emptyTaskDescriptionComment)
            return
        }

        timeTicker = Observable.interval(1, TimeUnit.MINUTES)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { minutes ->
                currentElapsedTime = LocalTimeCompanion.ofMinuteOfDay(minutes)
                updateNotification()
            }

        startDateTime = LocalDateTime.now()
        currentElapsedTime = LocalTime.MIN
        updateNotification()

        startedTask = Task(startDateTime!!, null, currentTaskDescription, currentColor)
        taskDao.insertTask(startedTask!!)
            .subscribeOn(Schedulers.io())
            .subscribe { id ->
                startedTask!!.id = id
            }
    }

    private fun stopTask() {
        if (!isTaskStarted()) {
            return
        }

        timeTicker?.dispose()
        updateNotification()

        val startedTask = startedTask!!
        val task = Task(
            startedTask.startDateTime,
            startedTask.startDateTime.plusMinutes(currentElapsedTime!!.toMinuteOfDay()),
            startedTask.desc,
            startedTask.colorInt
        ).also { it.id = startedTask.id }
        taskDao.updateTask(task)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    private fun isTaskStarted(): Boolean = timeTicker?.isDisposed == false

    private fun getCurrentNotificationView(): RemoteViews {
        val view = RemoteViews(packageName, R.layout.minidays_notification_view)

        if (currentTaskDescription.isEmpty()) {
            view.setTextViewText(R.id.task_description, emptyTaskDescriptionComment)
            view.setTextColor(R.id.task_description, greyColor)
        } else {
            view.setTextViewText(R.id.task_description, currentTaskDescription)
            view.setTextColor(R.id.task_description, blackColor)
        }

        view.setInt(R.id.thumbnail, "setColorFilter", currentColor)

        if (isTaskStarted()) {
            // 진행중
            view.setViewVisibility(R.id.elapsed_time, View.VISIBLE)
            view.setTextViewText(R.id.elapsed_time, currentElapsedTime?.toDurationDisplayFormat())
            view.setTextViewText(R.id.start_or_stop_button, stopText)
            view.setOnClickPendingIntent(R.id.start_or_stop_button, getStopTaskIntent())
        } else {
            view.setViewVisibility(R.id.elapsed_time, View.INVISIBLE)
            view.setTextViewText(R.id.start_or_stop_button, startText)
            view.setOnClickPendingIntent(R.id.start_or_stop_button, getStartTaskIntent())
        }

        return view
    }

    private fun getStartTaskIntent(): PendingIntent {
        val intent = Intent(this, MiniDaysService::class.java)
        intent.action = ACTION_START_TASK
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getStopTaskIntent(): PendingIntent {
        val intent = Intent(this, MiniDaysService::class.java)
        intent.action = ACTION_STOP_TASK
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getSimpleTaskEditActivityIntent(): PendingIntent {
        val intent = Intent(this, SimpleTaskEditDialogActivity::class.java)
        intent.putExtra(SimpleTaskEditDialogActivity.KEY_TASK_DESCRIPTION, currentTaskDescription)
        intent.putExtra(SimpleTaskEditDialogActivity.KEY_COLOR, currentColor)
        intent.putExtra(SimpleTaskEditDialogActivity.KEY_RESULT_RECEIVER, simpleTaskEditResultReceiver)
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onDestroy() {
        super.onDestroy()

        timeTicker?.dispose()
    }
}