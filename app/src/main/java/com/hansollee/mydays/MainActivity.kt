package com.hansollee.mydays

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity(), BackKeyDispatcher {

    companion object {
        private const val DRAWER_GRAVITY = Gravity.START
    }

    private val backKeyListeners: ArrayList<BackKeyListener> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tabLayout: TabLayout = findViewById(R.id.tablayout)
        val viewPager: ViewPager = findViewById(R.id.viewpager)
        val tabAdapter = MainTabAdapter(supportFragmentManager)
        val menuButton: View = findViewById(R.id.menu_button)
        val drawer: DrawerLayout = findViewById(R.id.drawer)
        val miniMyDaysButton: View = findViewById(R.id.mini_mydays_button)

        viewPager.adapter = tabAdapter
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.setTabTextColors(
            ContextCompat.getColor(this, R.color.tab_unselected),
            ContextCompat.getColor(this, R.color.tab_selected))

        menuButton.setOnClickListener { _ ->
            if (!drawer.isDrawerOpen(DRAWER_GRAVITY)) {
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
            super.onBackPressed()
        }
    }
}
