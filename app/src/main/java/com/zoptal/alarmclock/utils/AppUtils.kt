package com.zoptal.alarmclock.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast

object AppUtils {

    fun jumpToActivity(context: Context, activityClass: Class<*>) {
        val intent = Intent(context, activityClass)
        context.startActivity(intent)
    }

    fun showMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}