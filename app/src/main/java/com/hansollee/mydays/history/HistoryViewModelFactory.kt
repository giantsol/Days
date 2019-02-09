package com.hansollee.mydays.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.threeten.bp.LocalDate

/**
 * Created by kevin-ee on 2019-02-09.
 */

class HistoryViewModelFactory(private val today: LocalDate): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>?): T = HistoryViewModel(today) as T

}