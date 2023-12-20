package com.zoptal.alarmclock.listeners

import androidx.viewbinding.ViewBinding

interface RecyclerListener {
    fun onRecycleCreated(binding: ViewBinding,position: Int)
}