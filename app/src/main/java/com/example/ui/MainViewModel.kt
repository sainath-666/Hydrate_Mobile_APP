package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.HydrationDatabase
import com.example.data.HydrationPrefs
import com.example.data.HydrationRepository
import com.example.receiver.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = HydrationDatabase.getDatabase(application)
    val prefs = HydrationPrefs(application)
    private val repository = HydrationRepository(db.drinkDao(), prefs)

    val todayTotal = repository.todayTotalMl.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )
    
    val streak = repository.streakStats.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )
    
    val allRecords = repository.allRecords.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private val _settingsState = MutableStateFlow(SettingsState(
        remindersActive = prefs.remindersActive,
        startHour = prefs.startTimeHour,
        startMin = prefs.startTimeMinute,
        endHour = prefs.endTimeHour,
        endMin = prefs.endTimeMinute,
        intervalMins = prefs.intervalMinutes,
        dailyGoalMl = prefs.dailyGoalMl
    ))
    val settingsState = _settingsState.asStateFlow()

    init {
        ReminderScheduler.scheduleReminders(application, prefs)
    }

    fun addDrink(amountMl: Int, type: String = "Water") {
        viewModelScope.launch {
            repository.addDrink(amountMl, type)
        }
    }

    fun updateSettings(
        remindersActive: Boolean? = null,
        startHour: Int? = null,
        startMin: Int? = null,
        endHour: Int? = null,
        endMin: Int? = null,
        intervalMins: Int? = null,
        dailyGoalMl: Int? = null
    ) {
        remindersActive?.let { prefs.remindersActive = it }
        startHour?.let { prefs.startTimeHour = it }
        startMin?.let { prefs.startTimeMinute = it }
        endHour?.let { prefs.endTimeHour = it }
        endMin?.let { prefs.endTimeMinute = it }
        intervalMins?.let { prefs.intervalMinutes = it }
        dailyGoalMl?.let { prefs.dailyGoalMl = it }

        _settingsState.value = SettingsState(
            remindersActive = prefs.remindersActive,
            startHour = prefs.startTimeHour,
            startMin = prefs.startTimeMinute,
            endHour = prefs.endTimeHour,
            endMin = prefs.endTimeMinute,
            intervalMins = prefs.intervalMinutes,
            dailyGoalMl = prefs.dailyGoalMl
        )
        
        ReminderScheduler.scheduleReminders(getApplication(), prefs)
    }
}

data class SettingsState(
    val remindersActive: Boolean,
    val startHour: Int,
    val startMin: Int,
    val endHour: Int,
    val endMin: Int,
    val intervalMins: Int,
    val dailyGoalMl: Int
)
