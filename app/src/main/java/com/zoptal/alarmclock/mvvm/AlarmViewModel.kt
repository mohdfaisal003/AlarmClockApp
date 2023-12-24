package com.zoptal.alarmclock.mvvm

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zoptal.alarmclock.preferences.AlarmStorageHelper
import com.zoptal.alarmclock.utils.AlarmUtil
import com.zoptal.alarmclock.utils.AppUtils.showMessage
import kotlinx.coroutines.launch

class AlarmViewModel : ViewModel() {

    val error = MutableLiveData<String>()
    val selectAllAlarms = MutableLiveData<List<AlarmEntity>>()

    fun insertAlarm(context: Context, alarmEntity: AlarmEntity) {
        viewModelScope.launch {
            try {
                AlarmStorageHelper.saveAlarm(context, alarmEntity)
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
            } catch (exception: Exception) {
                exception.printStackTrace()
                error.value = exception.message
            }
        }
    }


    fun getAllAlarms(context: Context) {
        viewModelScope.launch {
            try {
                selectAllAlarms.value = AlarmStorageHelper.getAlarms(context)
            } catch (exception: Exception) {
                exception.printStackTrace()
                error.value = exception.message
            }
        }
    }

    fun updateAlarm(context: Context, alarmEntity: AlarmEntity) {
        viewModelScope.launch {
            try {
                AlarmStorageHelper.updateAlarm(context, alarmEntity)
                if (!alarmEntity.isEnable) {
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
            } catch (exception: Exception) {
                exception.printStackTrace()
                error.value = exception.message
            }
        }
    }
}