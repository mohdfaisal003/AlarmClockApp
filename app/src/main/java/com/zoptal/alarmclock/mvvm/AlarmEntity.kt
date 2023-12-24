package com.zoptal.alarmclock.mvvm

data class AlarmEntity(
    var id: Int,
    var hour: Int,
    var minute: Int,
    var am_pm: Int,
    var date: Int,
    var ringtoneUri: String,
    var alarmId: Int,
    var message: String,
    var isEnable: Boolean
)