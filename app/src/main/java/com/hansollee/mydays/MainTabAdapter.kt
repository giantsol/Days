package com.hansollee.mydays

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.hansollee.mydays.history.HistoryFragment
import com.hansollee.mydays.tasks.TasksFragment

/**
 * Created by kevin-ee on 2019-01-31.
 */

class MainTabAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val taskFragmentTitle: String
    private val historyFragmentTitle: String

    init {
        val res = appContext!!.resources
        taskFragmentTitle = res.getString(R.string.task_fragment_title)
        historyFragmentTitle = res.getString(R.string.history_fragment_title)
    }

    override fun getItem(position: Int): Fragment = if (position == 0) TasksFragment() else HistoryFragment()

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence
        = if (position == 0) taskFragmentTitle else historyFragmentTitle

}
