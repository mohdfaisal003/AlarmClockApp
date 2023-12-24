package com.zoptal.alarmclock.appbase

import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.zoptal.alarmclock.listeners.RecyclerListener

abstract class AppBaseActivity : AppCompatActivity(), OnClickListener, RecyclerListener {

    override fun onClick(view: View?) {/* Do your work */
    }

    override fun onRecycleCreated(binding: ViewBinding, position: Int) {/* Do your work */
    }
}