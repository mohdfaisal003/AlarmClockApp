package com.zoptal.alarmclock.room

import android.content.Context
import androidx.room.Room.databaseBuilder

class AlarmDatabaseClient(private var mCtx: Context) {
    private var mInstance: AlarmDatabaseClient? = null
    private var alarmDatabase: AlarmDatabase? = null

    init {
        alarmDatabase = databaseBuilder(mCtx, AlarmDatabase::class.java, "AlarmsDatabase").build()
    }

    @Synchronized
    fun getInstance(): AlarmDatabaseClient? {
        if (mInstance == null) {
            mInstance = AlarmDatabaseClient(mCtx)
        }
        return mInstance
    }

    fun getAlarmDatabase(): AlarmDatabase? {
        return alarmDatabase
    }
}