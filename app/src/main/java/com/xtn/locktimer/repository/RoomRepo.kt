package com.xtn.locktimer.repository

import com.xtn.locktimer.model.Battery
import com.xtn.locktimer.model.Clock
import com.xtn.locktimer.model.LockTimerInfo
import com.xtn.locktimer.model.Timer
import com.xtn.locktimer.repository.db.LockTimerDatabase
import javax.inject.Inject


class RoomRepo @Inject constructor(
    db: LockTimerDatabase
) {

    private val _lockTimerInfoDao = db.lockTimerInfoDao()
    private val _clockDao = db.clockDao()
    private val _timerDao = db.timerDao()
    private val _batteryDao = db.batteryDao()

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
            is LockTimerInfo -> _lockTimerInfoDao.insert(data)
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