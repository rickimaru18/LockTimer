package com.xtn.locktimer.repository.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.xtn.locktimer.model.Timer


@Dao
interface TimerDao {

    @Query("SELECT * FROM timer ORDER BY id DESC")
    fun getTimers() : LiveData<List<Timer>>

    @Query("SELECT * FROM timer WHERE minutes = :minutes")
    fun getTimer(minutes: Long) : Timer?

    @Insert
    fun insert(timer: Timer)

    @Update
    fun update(timer: Timer)

    @Delete
    fun delete(timer: Timer)

    @Query("DELETE FROM timer WHERE id = (SELECT id FROM timer LIMIT 1)")
    fun deleteTopRow()

}