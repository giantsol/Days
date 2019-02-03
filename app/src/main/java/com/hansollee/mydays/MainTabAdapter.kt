package com.hansollee.mydays

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.hansollee.mydays.history.HistoryFragment
import com.hansollee.mydays.task.TaskFragment

/**
 * Created by kevin-ee on 2019-01-31.
 */

class MainTabAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private var taskFragmentTitle: String = "Task"
    private var historyFragmentTitle: String = "History"

    init {
        appContext?.resources?.also {
            taskFragmentTitle = it.getString(R.string.task_fragment_title)
            historyFragmentTitle = it.getString(R.string.history_fragment_title)
        }
    }

    override fun getItem(position: Int): Fragment = if (position == 0) TaskFragment() else HistoryFragment()

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence
        = if (position == 0) taskFragmentTitle else historyFragmentTitle

}
