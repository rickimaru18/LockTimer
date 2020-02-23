package com.xtn.locktimer.repository.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.xtn.locktimer.model.Timer


@Dao
interface TimerDao {

    @Query("SELECT * FROM timer")
    fun getTimers() : LiveData<List<Timer>>

    @Insert
    fun insert(timer: Timer)

    @Update
    fun update(timer: Timer)

    @Delete
    fun delete(timer: Timer)

}