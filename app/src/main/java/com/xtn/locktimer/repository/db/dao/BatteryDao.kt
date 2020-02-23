package com.xtn.locktimer.repository.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.xtn.locktimer.model.Battery


@Dao
interface BatteryDao {

    @Query("SELECT * FROM battery")
    fun getBatteries() : LiveData<List<Battery>>

    @Insert
    fun insert(battery: Battery)

    @Update
    fun update(battery: Battery)

    @Delete
    fun delete(battery: Battery)

}