package com.xtn.locktimer.repository.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.xtn.locktimer.model.Clock


@Dao
interface ClockDao {

    @Query("SELECT * FROM clock")
    fun getClock() : LiveData<Clock?>

    @Insert
    fun insert(clock: Clock)

    @Update
    fun update(clock: Clock)

}