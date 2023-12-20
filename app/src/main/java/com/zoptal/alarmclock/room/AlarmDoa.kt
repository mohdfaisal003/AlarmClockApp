package com.zoptal.alarmclock.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AlarmDoa {

    @Query("SELECT * from alarmentity")
    fun alarmList(): List<AlarmEntity>

    @Insert
    fun createAlarm(alarmEntity: AlarmEntity)

    @Update
    fun updateAlarm(alarmEntity: AlarmEntity)
}