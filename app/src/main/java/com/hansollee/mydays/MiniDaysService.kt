package com.hansollee.mydays

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

/**
 * Created by kevin-ee on 2019-02-08.
 */

class MiniDaysService: Service() {
    companion object {
        const val ACTION_START_SERVICE = "action.start"
        private const val NOTIFICATION_CHANNEL_ID = "mini.days.channel"
        private const val NOTIFICATION_CHANNEL_NAME = "MiniDays"
        private const val SERVICE_ID = 1001
    }

    private var isForeground: Boolean = false
    private var currentTaskDescription = ""
    private var currentColor: Int = 0
    private var blackColor: Int = 0
    private var greyColor: Int = 0
    private lateinit var emptyTaskDescriptionComment: String

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
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            when (intent.action) {
                ACTION_START_SERVICE -> {
                    startForegroundIfNotStarted()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundIfNotStarted() {
        if (isForeground) {
            return
        }

        val view = getCurrentNotificationView()

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(view)
            .setContentIntent(getSimpleTaskEditActivityIntent())
            .build()

        startForeground(notification)
    }

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

        return view
    }

    private fun startForeground(notification: Notification) {
        startForeground(SERVICE_ID, notification)
        isForeground = true
    }

    private fun getSimpleTaskEditActivityIntent(): PendingIntent {
        val intent = Intent(this, SimpleTaskEditActivity::class.java)
        intent.putExtra(SimpleTaskEditActivity.KEY_CURRENT_TASK_DESCRIPTION, currentTaskDescription)
        intent.putExtra(SimpleTaskEditActivity.KEY_CURRENT_COLOR, currentColor)
        return PendingIntent.getActivity(this, 0, intent, 0)
    }
}