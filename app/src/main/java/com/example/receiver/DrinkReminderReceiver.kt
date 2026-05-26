package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.HydrationPrefs
import java.util.Calendar

class DrinkReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prefs = HydrationPrefs(context)
        if (!prefs.remindersActive) return

        val cal = Calendar.getInstance()
        val currentHour = cal.get(Calendar.HOUR_OF_DAY)
        val currentMinute = cal.get(Calendar.MINUTE)
        
        val currentTimeInMins = currentHour * 60 + currentMinute
        var startTimeInMins = prefs.startTimeHour * 60 + prefs.startTimeMinute
        var endTimeInMins = prefs.endTimeHour * 60 + prefs.endTimeMinute

        // Handle case where end time is past midnight (e.g. 23:00 to 02:00)
        val isWithinWindow = if (startTimeInMins <= endTimeInMins) {
            currentTimeInMins in startTimeInMins..endTimeInMins
        } else {
            currentTimeInMins >= startTimeInMins || currentTimeInMins <= endTimeInMins
        }

        if (isWithinWindow) {
            showNotification(context)
        }
    }

    private fun showNotification(context: Context) {
        val channelId = "hydration_reminders_channel"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Hydration Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) 
            .setContentTitle("Time to Hydrate! 💧")
            .setContentText("Stay on track with your water goal today.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        manager.notify(1001, builder.build())
    }
}
