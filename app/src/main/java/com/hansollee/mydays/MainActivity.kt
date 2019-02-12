package com.hansollee.mydays

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.hansollee.mydays.tasks.TasksViewModel

class MainActivity : AppCompatActivity(), BackKeyDispatcher {

    companion object {
        private const val DRAWER_GRAVITY = Gravity.START
        private const val SECOND_BACK_BUTTON_TO_QUIT_INTERVAL = 2000L
        private const val TABLAYOUT_ANIMATION_DURATION = 300L
    }

    private lateinit var globalViewModel: GlobalViewModel

    private val handler = Handler()
    private lateinit var pressAgainToQuitMsg: String
    private var isWaitingForSecondButtonToQuit = false
    private val resetWaitingForSecondButton = Runnable {
        isWaitingForSecondButtonToQuit = false
    }
    private lateinit var viewPager: ViewPager

    private val backKeyListeners: ArrayList<BackKeyListener> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        globalViewModel = GlobalViewModel.getInstance(this)
        val tasksViewModel = TasksViewModel.getInstance(this, globalViewModel.getTodayValue())
        val tabLayout: TabLayout = findViewById(R.id.tablayout)
        viewPager = findViewById(R.id.viewpager)
        val tabAdapter = MainTabAdapter(supportFragmentManager)
        val menuButton: View = findViewById(R.id.menu_button)
        val drawer: DrawerLayout = findViewById(R.id.drawer)
        val miniMyDaysButton: View = findViewById(R.id.mini_mydays_button)
        val loadingView: View = findViewById(R.id.loading_view)
        pressAgainToQuitMsg = getString(R.string.press_again_to_quit)

        viewPager.adapter = tabAdapter
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.setTabTextColors(
            ContextCompat.getColor(this, R.color.tab_unselected),
            ContextCompat.getColor(this, R.color.tab_selected)
        )

        menuButton.setOnClickListener { _ ->
            if (loadingView.visibility != View.VISIBLE && !drawer.isDrawerOpen(DRAWER_GRAVITY)) {
                drawer.openDrawer(DRAWER_GRAVITY)
            }
        }

        miniMyDaysButton.setOnClickListener { _ ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivityForResult(intent, 1000)
            } else {
                startFloatingWidgetService()
            }
        }

        tasksViewModel.getLoadingStatus().observe(this, Observer<Boolean> { isLoading ->
            if (isLoading) {
                loadingView.visibility = View.VISIBLE
            } else {
                loadingView.visibility = View.GONE
            }
        })
    }

    private fun startFloatingWidgetService() {
        startService(Intent(this, FloatingWidgetService::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                startFloatingWidgetService()
            } else {
                toast("Please!! Permission!!")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun addBackKeyListener(listener: BackKeyListener) {
        backKeyListeners.add(listener)
    }

    override fun removeBackKeyListener(listener: BackKeyListener) {
        backKeyListeners.remove(listener)
    }

    override fun dispatchBackKey(): Boolean {
        val listeners = backKeyListeners
        var isHandled = false

        for (index in listeners.size-1 downTo 0) {
            isHandled = isHandled || listeners[index].onBackPressed()
        }

        return isHandled
    }

    override fun onBackPressed() {
        if (!dispatchBackKey()) {
            if (isWaitingForSecondButtonToQuit) {
                handler.removeCallbacks(resetWaitingForSecondButton)
                super.onBackPressed()
            } else {
                beginWaitingForSecondBackButton()
                toast(pressAgainToQuitMsg)
            }
        }
    }

    private fun beginWaitingForSecondBackButton() {
        isWaitingForSecondButtonToQuit = true
        handler.postDelayed(resetWaitingForSecondButton, SECOND_BACK_BUTTON_TO_QUIT_INTERVAL)
    }

    override fun onStart() {
        super.onStart()
        globalViewModel.updateToday()
    }

    fun goToPage(page: Int, animate: Boolean, callbackWhenAnimate: () -> Unit) {
        viewPager.setCurrentItem(page, animate)

        if (animate) {
            handler.postDelayed(Runnable(callbackWhenAnimate), TABLAYOUT_ANIMATION_DURATION)
        }
    }
}
