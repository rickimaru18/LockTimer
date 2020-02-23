package com.xtn.locktimer.repository.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.xtn.locktimer.model.LockTimerInfo


@Dao
interface LockTimerInfoDao {

    @Query("SELECT * FROM lock_timer_info")
    fun getLockTimerInfo() : LiveData<LockTimerInfo?>

    @Insert
    fun insert(lockTimerInfo: LockTimerInfo)

    @Update
    fun update(lockTimerInfo: LockTimerInfo)

}