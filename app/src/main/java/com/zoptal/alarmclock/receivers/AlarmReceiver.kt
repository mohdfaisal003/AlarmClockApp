package com.zoptal.alarmclock.receivers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.zoptal.alarmclock.ui.AlarmsActivity
import com.zoptal.alarmclock.utils.AlarmUtil
import java.io.IOException

class AlarmReceiver : BroadcastReceiver(), AlarmUtil.AlarmListener {

    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var mediaPlayer: MediaPlayer? = null
    private var context: Context? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        this.context = context
        AlarmUtil.setAlarmListener(this)
        handler = Handler(Looper.getMainLooper())
        val message = intent?.getStringExtra("message")
        val ringtoneUriString = intent?.getStringExtra("ringtone")
        showNotification(context, "Zoptal Alarms", message.toString(), Uri.parse(ringtoneUriString))
    }

    private fun startSoundPlayer(context: Context, alarmUri: Uri) {
        runnable = Runnable {
            mediaPlayer = MediaPlayer()
            try {
                mediaPlayer?.setDataSource(context, alarmUri)
                mediaPlayer?.prepare()
                mediaPlayer?.start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        handler?.post(runnable!!)
    }

    private fun stopAlarm() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        handler?.removeCallbacks(runnable!!)
    }

    private fun showNotification(
        context: Context?, title: String, description: String, ringtoneUri: Uri
    ) {
        startSoundPlayer(context!!, ringtoneUri)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default_channel_id", "Zoptal Notifications", NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }

        val intent = Intent(context, AlarmsActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification =
            NotificationCompat.Builder(context, "default_channel_id").setContentTitle(title)
                .setContentText(description).setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm).setAutoCancel(false)
                .setSound(null).setPriority(NotificationCompat.PRIORITY_HIGH).build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager?.notify(1, notification)
    }

    override fun onAlarmTrigger(alarmId: Int, time: Long, isCreated: Boolean) {
        if (!isCreated) {
            stopAlarm()
            this.context?.let { cancelNotification(it) }
        }
    }

    private fun cancelNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)
    }
}