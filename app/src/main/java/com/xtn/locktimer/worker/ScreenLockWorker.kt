package com.xtn.locktimer.worker

import android.app.admin.DevicePolicyManager
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.xtn.locktimer.model.LockTimerInfo
import com.xtn.locktimer.repository.db.LockTimerDatabase
import com.xtn.locktimer.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ScreenLockWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    companion object {
        val WORKER_TAG = "scheduled_ScreenLockWorker"
    }

    override fun doWork(): Result {
        GlobalScope.launch(Dispatchers.IO) {
            LockTimerDatabase.getInstance(applicationContext)
                .lockTimerInfoDao()
                .update(LockTimerInfo(false, -1))
        }

        Logger.d("Locking phone... $applicationContext")
        val devicePolicyManager = applicationContext.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        devicePolicyManager.lockNow()
        return Result.success()
    }

}