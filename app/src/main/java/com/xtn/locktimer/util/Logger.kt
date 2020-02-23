package com.xtn.locktimer.util

import android.util.Log


object Logger {

    val TAG = "LockTimer"

    var enable = true

    /**
     * Debug log.
     *
     * @param msg   Log message.
     * @param tag   Log tag. Default value is "LockTimer".
     */
    fun d(msg: String, tag: String = TAG) {
        if (!enable) return
        Log.d("${tag}_d", msg)
    }

}