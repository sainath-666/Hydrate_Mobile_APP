package com.example.data

import android.content.Context
import android.content.SharedPreferences

class HydrationPrefs(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("hydration_prefs", Context.MODE_PRIVATE)

    var remindersActive: Boolean
        get() = prefs.getBoolean("reminders_active", true)
        set(value) = prefs.edit().putBoolean("reminders_active", value).apply()

    var startTimeHour: Int
        get() = prefs.getInt("start_time_hour", 7)
        set(value) = prefs.edit().putInt("start_time_hour", value).apply()

    var startTimeMinute: Int
        get() = prefs.getInt("start_time_minute", 0)
        set(value) = prefs.edit().putInt("start_time_minute", value).apply()

    var endTimeHour: Int
        get() = prefs.getInt("end_time_hour", 22)
        set(value) = prefs.edit().putInt("end_time_hour", value).apply()

    var endTimeMinute: Int
        get() = prefs.getInt("end_time_minute", 0)
        set(value) = prefs.edit().putInt("end_time_minute", value).apply()

    var intervalMinutes: Int
        get() = prefs.getInt("interval_minutes", 30)
        set(value) = prefs.edit().putInt("interval_minutes", value).apply()
        
    var dailyGoalMl: Int
        get() = prefs.getInt("daily_goal_ml", 2500)
        set(value) = prefs.edit().putInt("daily_goal_ml", value).apply()
}
