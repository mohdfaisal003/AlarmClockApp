package com.zoptal.alarmclock.mvvm

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zoptal.alarmclock.room.AlarmDatabaseClient
import com.zoptal.alarmclock.room.AlarmEntity
import com.zoptal.alarmclock.utils.AlarmUtil
import com.zoptal.alarmclock.utils.AppUtils.showMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmViewModel : ViewModel() {

    private var databaseClient: AlarmDatabaseClient? = null
    val error = MutableLiveData<String>()
    val selectAllAlarms = MutableLiveData<ArrayList<AlarmEntity>>()

    fun insertAlarm(context: Context, alarmEntity: AlarmEntity) {
        viewModelScope.launch {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    databaseClient = AlarmDatabaseClient(context).getInstance()
                    databaseClient?.getAlarmDatabase()
                        ?.alarmDao()
                        ?.createAlarm(alarmEntity)

                    withContext(Dispatchers.Main) {
                        AlarmUtil.createNewAlarm(
                            context,
                            alarmEntity.alarmId,
                            alarmEntity.hour,
                            alarmEntity.minute,
                            alarmEntity.am_pm,
                            alarmEntity.message,
                            Uri.parse(alarmEntity.ringtoneUri)
                        )
                        showMessage(
                            context,
                            "Alarm set for ${alarmEntity.hour}:${alarmEntity.minute} ${if (alarmEntity.am_pm == 0) "AM" else "PM"}"
                        )
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                error.value = exception.message
            }
        }
    }


    fun getAllAlarms(context: Context) {
        var value: ArrayList<AlarmEntity>
        viewModelScope.launch {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    databaseClient = AlarmDatabaseClient(context).getInstance()
                    value = databaseClient?.getAlarmDatabase()
                        ?.alarmDao()
                        ?.alarmList() as ArrayList<AlarmEntity>
                    withContext(Dispatchers.Main) {
                        selectAllAlarms.value = value
                    }
                }

            } catch (exception: Exception) {
                exception.printStackTrace()
                error.value = exception.message
            }
        }
    }

    fun updateAlarm(context: Context, alarmEntity: AlarmEntity) {
        viewModelScope.launch {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    databaseClient = AlarmDatabaseClient(context).getInstance()
                    databaseClient?.getAlarmDatabase()
                        ?.alarmDao()
                        ?.updateAlarm(alarmEntity)

                    withContext(Dispatchers.Main) {
                        if (alarmEntity.isEnable) {
                            AlarmUtil.cancelAlarm(context, alarmEntity.alarmId)
                        } else {
                            AlarmUtil.createNewAlarm(
                                context,
                                alarmEntity.alarmId,
                                alarmEntity.hour,
                                alarmEntity.minute,
                                alarmEntity.am_pm,
                                alarmEntity.message,
                                Uri.parse(alarmEntity.ringtoneUri)
                            )
                        }
                        showMessage(
                            context,
                            "Alarm updated to ${alarmEntity.hour}:${alarmEntity.minute} ${if (alarmEntity.am_pm == 0) "AM" else "PM"}"
                        )
                    }
                }

            } catch (exception: Exception) {
                exception.printStackTrace()
                error.value = exception.message
            }
        }
    }
}