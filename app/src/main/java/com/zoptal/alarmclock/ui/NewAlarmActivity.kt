package com.zoptal.alarmclock.ui

import android.Manifest
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.zoptal.alarmclock.R
import com.zoptal.alarmclock.appbase.AppBaseActivity
import com.zoptal.alarmclock.databinding.ActivityNewAlarmBinding
import com.zoptal.alarmclock.mvvm.AlarmEntity
import com.zoptal.alarmclock.mvvm.AlarmViewModel
import com.zoptal.alarmclock.utils.AlarmUtil
import com.zoptal.alarmclock.utils.AlarmUtil.get12HourTime
import com.zoptal.alarmclock.utils.AlarmUtil.getCurrentTime
import com.zoptal.alarmclock.utils.AlarmUtil.getHour
import com.zoptal.alarmclock.utils.AppUtils.showMessage
import kotlinx.coroutines.Runnable
import java.util.Calendar
import kotlin.random.Random

class NewAlarmActivity : AppBaseActivity() {

    private lateinit var binding: ActivityNewAlarmBinding
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null

    private val calendar: Calendar = Calendar.getInstance()
    private var hour = calendar.get(Calendar.HOUR_OF_DAY)
    private var minute = calendar.get(Calendar.MINUTE)
    private var am_pm = calendar.get(Calendar.AM_PM)

    private var selectedRingtone: Uri? = AlarmUtil.getCurrentAlarmTone()

    private val alarmViewModel by viewModels<AlarmViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* To show the current time */
        runnable = Runnable {
            binding.timeTv.text = getCurrentTime()
            handler.postDelayed(runnable!!, 1000)
        }
        handler.post(runnable!!)

        binding.backBtnIv.setOnClickListener(this)
        binding.timeTv.setOnClickListener(this)
        binding.saveBtn.setOnClickListener(this)
        binding.amTv.setOnClickListener(this)
        binding.pmTv.setOnClickListener(this)
        binding.ringtoneTv.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onClick(view: View?) {
        when (view?.id) {
            binding.backBtnIv.id -> {
                finish()
            }

            binding.timeTv.id -> {
                showTimePicker()
            }

            binding.saveBtn.id -> {
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(
                        this, Manifest.permission.POST_NOTIFICATIONS
                    ) -> {
                        if (calendar.timeInMillis == System.currentTimeMillis()) {
                            showMessage(this, "Please choose some other time")
                            return
                        } else if (binding.alarmNameEt.text.toString().isEmpty()) {
                            showMessage(this, "Please write a message for alarm")
                            return
                        } else {
                            setNewAlarm()
                        }
                    }

                    else -> {
                        pushNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            binding.amTv.id -> {
                am_pm = Calendar.AM
                binding.amTv.setTextColor(getColor(R.color.black))
                binding.pmTv.setTextColor(getColor(R.color.grey_clr))
            }

            binding.pmTv.id -> {
                am_pm = Calendar.PM
                binding.amTv.setTextColor(getColor(R.color.grey_clr))
                binding.pmTv.setTextColor(getColor(R.color.black))
            }

            binding.ringtoneTv.id -> {
                selectRingtone()
            }
        }
    }

    private fun setNewAlarm() {
        val alarmEntity = AlarmEntity(
            0,
            hour,
            minute,
            am_pm,
            Calendar.getInstance().get(Calendar.DATE),
            selectedRingtone.toString(),
            Random.nextInt(1000),
            binding.alarmNameEt.text.toString(),
            true
        )
        alarmViewModel.insertAlarm(this, alarmEntity)
    }

    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(
            this, { picker, selectedHour, selectedMinute ->
                hour = getHour(selectedHour)
                minute = selectedMinute
                binding.timeTv.text = get12HourTime(selectedHour, selectedMinute)
                runnable?.let { handler.removeCallbacks(it) }
            }, hour, minute, false
        )

        timePickerDialog.setTitle("Select Time")
        timePickerDialog.show()
    }

    private val pushNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) if (selectedRingtone != null) {
                setNewAlarm()
            }
        }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                if (data != null && data.hasExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)) {
                    val pickedUri =
                        data.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                    selectedRingtone = pickedUri
                    binding.ringtoneTv.text =
                        RingtoneManager.getRingtone(this, selectedRingtone).getTitle(this)
                } else {
                    selectedRingtone = null
                }
            }
        }

    private fun selectRingtone() {
        // Check if the WRITE_SETTINGS permission is not granted
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(
            RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM
        )
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, selectedRingtone)
        getContent.launch(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        runnable?.let { handler.removeCallbacks(it) }
    }
}