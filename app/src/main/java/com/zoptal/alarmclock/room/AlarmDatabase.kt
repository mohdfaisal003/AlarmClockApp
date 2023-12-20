package com.zoptal.alarmclock.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(AlarmEntity::class), version = 1, exportSchema = false)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDoa?
}