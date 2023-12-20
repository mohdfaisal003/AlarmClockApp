package com.zoptal.alarmclock.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.zoptal.alarmclock.receivers.AlarmReceiver
import com.zoptal.alarmclock.utils.AppUtils.showMessage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object AlarmUtil {

    fun getHour(hour24Format: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour24Format)
        val dateFormat = SimpleDateFormat("hh", Locale.getDefault())
        return dateFormat.format(calendar.time).toInt()
    }

    fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
        return dateFormat.format(System.currentTimeMillis())
    }

    fun get12HourTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        val dateFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun timeZone(): TimeZone {
        val timeZone = TimeZone.getTimeZone("Asia/Kolkata")
        TimeZone.setDefault(timeZone)
        return timeZone
    }

    fun createNewAlarm(
        context: Context,
        alarmId: Int,
        hour: Int,
        minute: Int,
        am_pm: Int,
        message: String,
        ringtone: Uri
    ) {
        val calendar: Calendar = Calendar.getInstance(timeZone(), Locale.getDefault()).apply {

            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.AM_PM, am_pm)
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("message", message)
            putExtra("ringtone", ringtone.toString())
        }
        val pendingIntent =
            PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_MUTABLE)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        alarmManager?.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        showMessage(context, "Alarm Set!")

//        val activity = context as Activity
//        activity.finish()
    }

    fun cancelAlarm(context: Context, alarmId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_MUTABLE)
        pendingIntent?.let { alarmManager?.cancel(it) }
    }
}