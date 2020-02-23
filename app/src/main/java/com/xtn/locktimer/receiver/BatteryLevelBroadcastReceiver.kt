package com.xtn.locktimer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.widget.Toast
import com.xtn.locktimer.repository.db.LockTimerDatabase
import com.xtn.locktimer.worker.WorkerUtil


class BatteryLevelBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, batteryStatus: Intent?) {
        val currentBattery = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        }?.toInt()
        Toast.makeText(context, "Battery Level: $currentBattery", Toast.LENGTH_LONG).show()
        val targetBattery = LockTimerDatabase.getInstance(context?.applicationContext!!)
            .batteryDao()
            .getBatteries()
            .value.run {
                if (this?.isEmpty()!!) -1
                last().percentage
            }

        Toast.makeText(context, "Battery Level: $currentBattery", Toast.LENGTH_LONG).show()
        if (targetBattery == currentBattery) {
            WorkerUtil.scheduleScreenLockWorker(context!!, 0L)
        }
    }

}