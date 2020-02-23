package com.xtn.locktimer.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.xtn.locktimer.util.Logger
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit


object WorkerUtil {

    private var _batteryBroadcastReceiver: BroadcastReceiver? = null

    /**
     * Schedule screen lock using the time of the day.
     *
     * @param context   Context.
     * @param screenLockTime    Time of the day.
     */
    fun scheduleScreenLockWorker(context: Context, screenLockTime: LocalTime) {
        val clockInSeconds = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).run {
            val timeNow = toLocalTime()
            var newDate: LocalDateTime = this

            if (timeNow == screenLockTime || timeNow.isAfter(screenLockTime)) {
                newDate = plusDays(1)
            }

            newDate = newDate.withHour(screenLockTime.hour)
                .withMinute(screenLockTime.minute)
                .withSecond(0)
                .truncatedTo(ChronoUnit.SECONDS)
            Duration.between(this, newDate).toMillis() / 1000
        }
        scheduleScreenLockWorker(context, clockInSeconds, TimeUnit.SECONDS)
    }

    /**
     * Schedule screen lock using a delay timer.
     *
     * @param context   Context.
     * @param delay    Delay in time unit.
     * @param timeUnit  Time unit of the delay. Default value is TimeUnit.MINUTES.
     */
    fun scheduleScreenLockWorker(context: Context, delay: Long, timeUnit: TimeUnit = TimeUnit.MINUTES) {
        Logger.d("Locking in $delay $timeUnit")
        val screenLockWorker = OneTimeWorkRequestBuilder<ScreenLockWorker>()
            .addTag(ScreenLockWorker.WORKER_TAG)
            .setInitialDelay(delay, timeUnit)
            .build()
        WorkManager.getInstance(context).apply {
            enqueue(screenLockWorker)
        }
    }

    /**
     * Schedule screen lock using the battery percentage.
     *
     * @param context   Context.
     * @param batteryPercentage    Target battery percentage on when the screen lock is executed.
     */
    fun scheduleScreenLockWorker(context: Context, batteryPercentage: Int) {
        Logger.d("Locking when battery is ${batteryPercentage}%")
        _batteryBroadcastReceiver = object: BroadcastReceiver() {

            override fun onReceive(context: Context?, batteryStatus: Intent?) {
                val batteryPct: Float? = batteryStatus?.let { intent ->
                    val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    level * 100 / scale.toFloat()
                }

                if (batteryPercentage == batteryPct!!.toInt()) {
                    stopBatteryBroadcastReceiver(context!!)
                    scheduleScreenLockWorker(context!!, 0L)
                }
            }

        }
        IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(
                _batteryBroadcastReceiver,
                ifilter
            )
        }
    }

    /**
     * Stop scheduled screen lock.
     *
     * @param context   Context.
     */
    fun stopScreenLockWorker(context: Context) {
        Logger.d("Stopping scheduled screen lock.")
        stopBatteryBroadcastReceiver(context)
        WorkManager.getInstance(context).cancelAllWorkByTag(ScreenLockWorker.WORKER_TAG)
    }

    /**
     * Stop battery broadcast receiver.
     *
     * @param context   Context.
     */
    private fun stopBatteryBroadcastReceiver(context: Context) {
        if (_batteryBroadcastReceiver == null) return
        context.unregisterReceiver(_batteryBroadcastReceiver)
        _batteryBroadcastReceiver = null
    }

}