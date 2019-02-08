package com.hansollee.mydays

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager

/**
 * Created by kevin-ee on 2019-02-08.
 */

class FloatingWidgetService: Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var floatingWidgetView: View
    private lateinit var collapsedView: View
    private lateinit var expandedView: View
    private lateinit var removeFloatingWidgetViewContainer: View
    private lateinit var removeFloatingWidgetView: View

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        addFloatingWidgetView(inflater)
        addRemoveAreaView(inflater)
    }

    private fun addFloatingWidgetView(inflater: LayoutInflater) {
        floatingWidgetView = inflater.inflate(R.layout.view_floating_widget, null)

        var type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
            )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = 100

        windowManager.addView(floatingWidgetView, params)

        collapsedView = floatingWidgetView.findViewById(R.id.collapsed_view)
        expandedView = floatingWidgetView.findViewById(R.id.expanded_view)

        collapsedView.findViewById<View>(R.id.collapsed_close_button).setOnClickListener { _ ->
            stopSelf()
        }
        collapsedView.findViewById<View>(R.id.collapsed_floating_icon).setOnClickListener { _ ->
            collapsedView.visibility = View.GONE
            expandedView.visibility = View.VISIBLE
        }

        expandedView.findViewById<View>(R.id.expanded_minimize_button).setOnClickListener { _ ->
            collapsedView.visibility = View.VISIBLE
            expandedView.visibility = View.GONE
        }
        expandedView.findViewById<View>(R.id.expanded_open_activity_button).setOnClickListener { _ ->
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

    }

    private fun addRemoveAreaView(inflater: LayoutInflater) {
        removeFloatingWidgetViewContainer = inflater.inflate(R.layout.view_remove_floating_widget, null)

        var type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START

        removeFloatingWidgetViewContainer.visibility = View.GONE
        removeFloatingWidgetView = removeFloatingWidgetViewContainer.findViewById(R.id.remove_area)

        windowManager.addView(removeFloatingWidgetViewContainer, params)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (::floatingWidgetView.isInitialized) {
            windowManager.removeView(floatingWidgetView)
        }

        if (::removeFloatingWidgetViewContainer.isInitialized) {
            windowManager.removeView(removeFloatingWidgetViewContainer)
        }

    }
}