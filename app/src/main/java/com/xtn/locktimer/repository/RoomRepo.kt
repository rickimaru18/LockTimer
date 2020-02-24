package com.xtn.locktimer.repository

import android.app.Application
import com.xtn.locktimer.model.Battery
import com.xtn.locktimer.model.Clock
import com.xtn.locktimer.model.LockTimerInfo
import com.xtn.locktimer.model.Timer
import com.xtn.locktimer.repository.db.LockTimerDatabase
import com.xtn.locktimer.repository.db.dao.BatteryDao
import com.xtn.locktimer.repository.db.dao.ClockDao
import com.xtn.locktimer.repository.db.dao.LockTimerInfoDao
import com.xtn.locktimer.repository.db.dao.TimerDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalTime


class RoomRepo {

    private val _lockTimerInfoDao: LockTimerInfoDao
    private val _clockDao: ClockDao
    private val _timerDao: TimerDao
    private val _batteryDao: BatteryDao

    companion object {
        private var mInstance: RoomRepo? = null

        /**
         * Get singleton instance.
         *
         * @return Singleton instance.
         */
        @Synchronized
        fun getInstance(application: Application) : RoomRepo {
            if (mInstance == null) {
                mInstance = RoomRepo(application)
            }
            return mInstance!!
        }
    }

    private constructor(application: Application) {
        val db = LockTimerDatabase.getInstance(application.applicationContext) { db ->
            // on DB created callback
            GlobalScope.launch(Dispatchers.IO) {
                db.lockTimerInfoDao().insert(LockTimerInfo(
                    false,
                    -1
                ))

                db.clockDao().insert(Clock(
                    LocalTime.now().hour,
                    LocalTime.now().minute
                ))

                db.timerDao().apply {
                    insert(Timer(20))
                    insert(Timer(30))
                    insert(Timer(50))
                    insert(Timer(60))
                    insert(Timer(90))
                }

                db.batteryDao().apply {
                    insert(Battery(50))
                    insert(Battery(40))
                    insert(Battery(30))
                    insert(Battery(20))
                    insert(Battery(10))
                }
            }
        }

        _lockTimerInfoDao = db.lockTimerInfoDao()
        _clockDao = db.clockDao()
        _timerDao = db.timerDao()
        _batteryDao = db.batteryDao()
    }

    /**
     * Get [LockTimerInfo] data from database.
     *
     * @return [LockTimerInfo] data.
     */
    fun getLockTimerInfo() = _lockTimerInfoDao.getLockTimerInfo()

    /**
     * Get [Clock] data from database.
     *
     * @return [Clock] data.
     */
    fun getClock() = _clockDao.getClock()

    /**
     * Get list of [Timer] data from database.
     *
     * @return List of [Timer].
     */
    fun getTimers() = _timerDao.getTimers()

    /**
     * Get list of [Battery] data from database.
     *
     * @return List of [Battery].
     */
    fun getBatteries() = _batteryDao.getBatteries()

    /**
     * Insert data to database.
     *
     * @param data  [Timer] or [Battery] instance.
     */
    fun insert(data: Any) {
        when (data) {
            is Timer -> _timerDao.insert(data)
            is Battery -> _batteryDao.insert(data)
        }
    }

    /**
     * Insert unique data to database.
     * If not unique, it will delete the old data.
     *
     * @param data  [Timer] or [Battery] instance.
     * @return TRUE if [data] is unique, FALSE otherwise.
     */
    fun insertUnique(data: Any) : Boolean {
        var isUnique = false

        when (data) {
            is Timer -> {
                val existData = _timerDao.getTimer(data.minutes)
                if (existData != null) {
                    isUnique = true
                    _timerDao.delete(existData)
                }
                insert(data)
            }
            is Battery -> {
                val existData = _batteryDao.getBattery(data.percentage)
                if (existData != null) {
                    isUnique = true
                    _batteryDao.delete(existData)
                }
                insert(data)
            }
        }

        return isUnique
    }

    /**
     * Update data in database.
     *
     * @param data  [LockTimerInfo], [Clock], [Timer] or [Battery] instance.
     */
    fun update(data: Any) {
        when (data) {
            is LockTimerInfo -> _lockTimerInfoDao.update(data)
            is Clock -> _clockDao.update(data)
            is Timer -> _timerDao.update(data)
            is Battery -> _batteryDao.update(data)
        }
    }

    /**
     * Delete data in database.
     *
     * @param data  [Timer] or [Battery] instance.
     */
    fun delete(data: Any) {
        when (data) {
            is Timer -> _timerDao.delete(data)
            is Battery -> _batteryDao.delete(data)
        }
    }

    /**
     * Delete top row data in database.
     *
     * @param tableName  [Timer.TABLE_NAME] or [Battery.TABLE_NAME].
     */
    fun deleteTopRow(tableName: String) {
        when (tableName) {
            Timer.TABLE_NAME -> _timerDao.deleteTopRow()
            Battery.TABLE_NAME -> _batteryDao.deleteTopRow()
        }
    }

}