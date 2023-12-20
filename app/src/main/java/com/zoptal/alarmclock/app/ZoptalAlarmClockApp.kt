package com.zoptal.alarmclock.app

import android.app.Application
import com.zoptal.alarmclock.room.AlarmDatabaseClient

class ZoptalAlarmClockApp : Application() {

    lateinit var alarmDatabaseClient: AlarmDatabaseClient

    override fun onCreate() {
        super.onCreate()
        alarmDatabaseClient = AlarmDatabaseClient(this)
        alarmDatabaseClient.getInstance()
    }
}