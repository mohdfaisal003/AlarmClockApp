package com.zoptal.alarmclock.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.zoptal.alarmclock.databinding.RvAlarmItemBinding
import com.zoptal.alarmclock.listeners.RecyclerListener
import com.zoptal.alarmclock.mvvm.AlarmEntity

class AlarmAdapter(var context: Context) : Adapter<AlarmAdapter.ViewHolder>() {

    private lateinit var binding: RvAlarmItemBinding
    private var listener: RecyclerListener? = null
    private var alarmsList = ArrayList<AlarmEntity>()

    constructor(context: Context, alarmsList: List<AlarmEntity>) : this(context) {
        this.alarmsList.addAll(alarmsList)
    }

    fun setRecyclerListener(listener: RecyclerListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = RvAlarmItemBinding.inflate(LayoutInflater.from(context))
        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return if (alarmsList.isNotEmpty()) alarmsList.size else 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        listener?.onRecycleCreated(binding, position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}