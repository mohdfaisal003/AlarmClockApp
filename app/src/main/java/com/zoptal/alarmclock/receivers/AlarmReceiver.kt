package com.zoptal.alarmclock.receivers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("message")
        val ringtoneUriString = intent?.getStringExtra("ringtone")
        showNotification(context, "Zoptal Alarms", message.toString(), Uri.parse(ringtoneUriString))
    }

    private fun showNotification(
        context: Context?,
        title: String,
        description: String,
        ringtoneUri: Uri
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default_channel_id", "Zoptal Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = context?.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }

        val soundUri: Ringtone = RingtoneManager.getRingtone(context, ringtoneUri)
        soundUri.play()
        val notification: Notification = NotificationCompat.Builder(context!!, "default_channel_id")
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setAutoCancel(true)
            .build()


        val notificationManager = context?.getSystemService(NotificationManager::class.java)
        notificationManager?.notify(1, notification)

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
              if (soundUri.isPlaying) {
                  soundUri.stop()
              }
        },10L)
    }
}