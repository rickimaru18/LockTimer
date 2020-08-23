package com.xtn.locktimer.worker

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.xtn.locktimer.util.Logger
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit


object WorkerUtil {

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
        scheduleScreenLockWorker(context, clockInSeconds)
    }

    /**
     * Schedule screen lock using a delay timer.
     *
     * @param context   Context.
     * @param delay    Delay in seconds.
     */
    fun scheduleScreenLockWorker(
        context: Context,
        delay: Long
    ) = runScreenLockWorker(
        context,
        Data.Builder()
            .putLong("delay", delay)
            .build()
    )

    /**
     * Schedule screen lock using the battery percentage.
     *
     * @param context   Context.
     * @param batteryPercentage    Target battery percentage on when the screen lock is executed.
     */
    fun scheduleScreenLockWorker(
        context: Context,
        batteryPercentage: Int
    ) = runScreenLockWorker(
        context,
        Data.Builder()
            .putLong("delay", batteryPercentage.toLong())
            .putBoolean("isTypeBattery", true)
            .build()
    )

    /**
     * Run [ScreenLockWorker].
     *
     * @param context   Context.
     * @param data  Input data.
     */
    private fun runScreenLockWorker(context: Context, data: Data) {
        val screenLockWorker = OneTimeWorkRequestBuilder<ScreenLockWorker>()
            .addTag(ScreenLockWorker.WORKER_TAG)
            .setInputData(data)
            .build()
        WorkManager.getInstance(context).enqueue(screenLockWorker)
    }

    /**
     * Stop scheduled screen lock.
     *
     * @param context   Context.
     */
    fun stopScreenLockWorker(context: Context) {
        Logger.d("Stopping scheduled screen lock.")
        WorkManager.getInstance(context).cancelAllWorkByTag(ScreenLockWorker.WORKER_TAG)
    }

}