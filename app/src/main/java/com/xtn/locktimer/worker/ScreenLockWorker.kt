package com.xtn.locktimer.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.*
import com.xtn.locktimer.model.LockTimerInfo
import com.xtn.locktimer.util.Logger
import com.xtn.locktimer.R
import com.xtn.locktimer.repository.RoomRepo
import com.xtn.locktimer.ui.MainActivity
import com.xtn.locktimer.util.Utils
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ScreenLockWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val roomRepo: RoomRepo
) : CoroutineWorker(appContext, workerParams) {

    private val NOTIFICATION_CHANNEL = "locktimer_channel"

    private val _notificationManager = appContext.getSystemService(
        NOTIFICATION_SERVICE
    ) as NotificationManager

    private val _delayInSeconds = inputData.getLong("delay", 0)
    private val _isTypeBattery = inputData.getBoolean("isTypeBattery", false)

    private var _batteryBroadcastReceiver: BroadcastReceiver? = null
    private var _isTargetBatteryAchieved = false

    companion object {
        val WORKER_TAG = "scheduled_ScreenLockWorker"
    }

    override suspend fun doWork(): Result {
        setForeground(createForegroundInfo())

        if (_isTypeBattery) {
            setupBatteryBroadcastReceiver()

            while (!_isTargetBatteryAchieved) {
                delay(60000)
            }

            applicationContext.unregisterReceiver(_batteryBroadcastReceiver)
        } else {
            delay(_delayInSeconds * 1000)
        }

        performLock()
        return Result.success()
    }

    /**
     * Create [ForegroundInfo] for the foreground service.
     *
     * @return [ForegroundInfo] of this worker.
     */
    private fun createForegroundInfo(): ForegroundInfo {
        val text: String = if (_isTypeBattery) {
            "${applicationContext.getString(R.string.notification_text_battery)} $_delayInSeconds%"
        } else {
            val dateTime = LocalDateTime.now()
                .plusSeconds(_delayInSeconds)
                .format(DateTimeFormatter.ofPattern(
                    if (Utils.isLanguageJapanese()) "ahh:mm" else "hh:mm a"
                ))
            "${applicationContext.getString(R.string.notification_text)} $dateTime"
        }
        val openIntent = PendingIntent.getActivity(
            applicationContext,
            1,
            Intent(applicationContext, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val cancelIntent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)

        createNotificationChannel()

        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
            .setSmallIcon(
                if (_isTypeBattery) R.drawable.ic_battery_24dp
                else R.drawable.ic_clock_24dp
            )
            .setContentTitle(text)
            .setOngoing(true)
            .setContentIntent(openIntent)
            .addAction(
                R.drawable.ic_stop_24dp,
                applicationContext.getString(R.string.cancel),
                cancelIntent
            )
            .build()

        return ForegroundInfo(1, notification)
    }

    /**
     * Create notification channel.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        var notificationChannel = _notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL)

        if (notificationChannel == null) {
            notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL,
                "LockTimer",
                NotificationManager.IMPORTANCE_LOW
            )
            _notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    /**
     * Setup broadcast receiver which listens to battery changes.
     */
    private fun setupBatteryBroadcastReceiver() {
        _batteryBroadcastReceiver = object: BroadcastReceiver() {

            override fun onReceive(context: Context?, batteryStatus: Intent?) {
                val batteryPct: Float? = batteryStatus?.let { intent ->
                    val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    level * 100 / scale.toFloat()
                }

                Logger.d("Battery changed... ${batteryPct!!.toInt()}")
                if (_delayInSeconds.toInt() == batteryPct!!.toInt()) {
                    Logger.d("Target battery achieved: $_delayInSeconds")
                    _isTargetBatteryAchieved = true

                }
            }

        }
        IntentFilter(Intent.ACTION_BATTERY_CHANGED).let {
            applicationContext.registerReceiver(
                _batteryBroadcastReceiver,
                it
            )
        }
    }

    /**
     * Perform phone lock.
     */
    private fun performLock() {
        roomRepo.update(LockTimerInfo(false, -1))

        Logger.d("Locking phone... $applicationContext")
        val devicePolicyManager = applicationContext.getSystemService(
            Context.DEVICE_POLICY_SERVICE
        ) as DevicePolicyManager
        devicePolicyManager.lockNow()
    }

}