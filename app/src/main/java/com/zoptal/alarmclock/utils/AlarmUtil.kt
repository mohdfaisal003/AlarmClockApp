package com.zoptal.alarmclock.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import com.zoptal.alarmclock.receivers.AlarmReceiver
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object AlarmUtil {

    fun getCurrentAlarmTone(): Uri {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    }

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

    interface AlarmListener {
        fun onAlarmTrigger(alarmId: Int, time: Long, isCreated: Boolean)
    }

    var listener: AlarmListener? = null

    fun setAlarmListener(listener: AlarmListener) {
        this.listener = listener
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

        val currentCal = Calendar.getInstance()
        val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault()).apply {
            set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
            set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH))

            Log.d(
                "currentCal",
                "${getHour(currentCal.get(Calendar.HOUR_OF_DAY))}:${currentCal.get(Calendar.MINUTE)} ${
                    currentCal.get(Calendar.AM_PM)
                }"
            )
            when {
                hour < getHour(currentCal.get(Calendar.HOUR_OF_DAY)) || minute < currentCal.get(
                    Calendar.MINUTE
                ) || am_pm != currentCal.get(Calendar.AM_PM) -> {
                    set(
                        Calendar.DAY_OF_MONTH,
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 1
                    )
                }

                else -> {
                    set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                }
            }
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.AM_PM, am_pm)
        }

        val format = SimpleDateFormat(
            "hh:mm a MMM/dd/yyyy",
            Locale.getDefault()
        ).format(Date(calendar.timeInMillis))
        Log.d("format", format.toString())

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("message", message)
            putExtra("ringtone", ringtone.toString())
        }
        val pendingIntent =
            PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_MUTABLE)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        alarmManager?.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        listener?.onAlarmTrigger(alarmId, calendar.timeInMillis, true)
    }

    fun cancelAlarm(context: Context, alarmId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_MUTABLE)
        pendingIntent?.let { alarmManager?.cancel(it) }
        listener?.onAlarmTrigger(alarmId, 0, false)
    }
}