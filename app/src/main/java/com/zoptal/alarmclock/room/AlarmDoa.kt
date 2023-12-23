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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createAlarm(vararg alarmEntity: AlarmEntity)

    @Update
    fun updateAlarm(alarmEntity: AlarmEntity)
}