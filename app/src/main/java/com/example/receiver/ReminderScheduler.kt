package com.example.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.data.HydrationPrefs

object ReminderScheduler {

    fun scheduleReminders(context: Context, prefs: HydrationPrefs) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DrinkReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Cancel existing
        alarmManager.cancel(pendingIntent)

        if (prefs.remindersActive) {
            val intervalMillis = prefs.intervalMinutes * 60 * 1000L
            val triggerTime = System.currentTimeMillis() + intervalMillis
            
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                intervalMillis,
                pendingIntent
            )
        }
    }
}
