package com.hansollee.mydays

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.hansollee.mydays.history.HistoryFragment
import com.hansollee.mydays.record.RecordFragment

/**
 * Created by kevin-ee on 2019-01-31.
 */

class MainTabAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = if (position == 0) RecordFragment() else HistoryFragment()

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence = if (position == 0) "Record" else "History"

}
