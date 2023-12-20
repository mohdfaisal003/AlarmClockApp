package com.zoptal.alarmclock.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarmentity")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(name = "hour")
    var hour: Int,
    @ColumnInfo(name = "minute")
    var minute: Int,
    @ColumnInfo(name = "am_pm")
    var am_pm: Int,
    @ColumnInfo(name = "date")
    var date: Int,
    @ColumnInfo(name = "ringtoneUri")
    var ringtoneUri: String,
    @ColumnInfo(name = "alarmId")
    var alarmId: Int,
    @ColumnInfo(name = "message")
    var message: String,
    @ColumnInfo(name = "isEnable")
    var isEnable: Boolean
)