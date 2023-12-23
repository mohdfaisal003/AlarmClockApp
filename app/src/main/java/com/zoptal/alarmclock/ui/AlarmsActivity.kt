package com.zoptal.alarmclock.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.zoptal.alarmclock.adapters.AlarmAdapter
import com.zoptal.alarmclock.appbase.AppBaseActivity
import com.zoptal.alarmclock.databinding.ActivityAlarmsBinding
import com.zoptal.alarmclock.databinding.RvAlarmItemBinding
import com.zoptal.alarmclock.mvvm.AlarmViewModel
import com.zoptal.alarmclock.room.AlarmEntity
import com.zoptal.alarmclock.utils.AlarmUtil
import com.zoptal.alarmclock.utils.AppUtils.jumpToActivity

class AlarmsActivity : AppBaseActivity(), AlarmUtil.AlarmListener {

    private lateinit var binding: ActivityAlarmsBinding
    lateinit var adapter: AlarmAdapter
    private val alarmViewModel by viewModels<AlarmViewModel>()
    private var alarmsList = ArrayList<AlarmEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addBtnIv.setOnClickListener {
            jumpToActivity(this, NewAlarmActivity::class.java)
        }

        setRecyclerView()
        AlarmUtil.setAlarmListener(this)
    }

    private fun setRecyclerView() {
        alarmViewModel.getAllAlarms(this)
        alarmViewModel.selectAllAlarms.observe(this, Observer {
            if (it.isNotEmpty()) {
                alarmsList = it
                binding.recyclerView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
                adapter = AlarmAdapter(this, alarmsList)
                adapter.setRecyclerListener(this)
                binding.recyclerView.adapter = adapter
            }
        })
    }

    override fun onRecycleCreated(binding: ViewBinding, position: Int) {
        val bind = binding as RvAlarmItemBinding
        val alarmEntity = alarmsList[position]
        bind.timeTv.text = "${alarmEntity.hour}:${alarmEntity.minute}"
        bind.amPmTv.text = "${if (alarmEntity.am_pm == 0) "AM" else "PM"}"
        bind.switchId.isChecked = alarmEntity.isEnable

        bind.switchId.setOnClickListener {
            if (alarmEntity.isEnable) {
                alarmEntity.isEnable = false
                alarmViewModel.updateAlarm(this, alarmEntity)
            } else {
                alarmEntity.isEnable = true
                alarmViewModel.updateAlarm(this, alarmEntity)
            }
        }
    }

    override fun onAlarmTrigger(alarmId: Int, time: Long, isCreated: Boolean) {
        setRecyclerView()
    }
}