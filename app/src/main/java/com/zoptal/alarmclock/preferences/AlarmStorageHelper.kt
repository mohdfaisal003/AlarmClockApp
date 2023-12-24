package com.zoptal.alarmclock.preferences

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zoptal.alarmclock.mvvm.AlarmEntity

object AlarmStorageHelper {

    private val gson = Gson()
    val prefName = "alarmsPrefs"

    fun sharedPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    }

    fun saveAlarm(context: Context, alarmEntity: AlarmEntity) {
        val alarms = getAlarms(context).toMutableList()
        alarms.add(alarmEntity)
        saveAlarms(context, alarms)
    }

    fun getAlarms(context: Context): List<AlarmEntity> {
        val alarmsJson = sharedPreference(context).getString(prefName, null)
        return if (alarmsJson != null) {
            gson.fromJson(alarmsJson, object : TypeToken<List<AlarmEntity>>() {}.type)
        } else {
            emptyList()
        }
    }

    private fun saveAlarms(context: Context, alarms: List<AlarmEntity>) {
        val alarmsJson = gson.toJson(alarms)
        sharedPreference(context).edit().putString(prefName, alarmsJson).apply()
    }

    fun updateAlarm(context: Context, updatedAlarm: AlarmEntity) {
        val alarms = getAlarms(context).toMutableList()
        val index = alarms.indexOfFirst { it.alarmId == updatedAlarm.alarmId }
        if (index != -1) {
            alarms[index] = updatedAlarm
            saveAlarms(context, alarms)
        }
    }
}