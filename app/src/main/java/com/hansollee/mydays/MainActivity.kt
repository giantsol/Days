package com.hansollee.mydays

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity(), BackKeyDispatcher {

    private lateinit var tabAdapter: MainTabAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    private val backKeyListeners: ArrayList<BackKeyListener> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabLayout = findViewById(R.id.tablayout)
        viewPager = findViewById(R.id.viewpager)

        tabAdapter = MainTabAdapter(supportFragmentManager)
        viewPager.adapter = tabAdapter
        tabLayout.setupWithViewPager(viewPager)

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
