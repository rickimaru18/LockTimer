package com.xtn.locktimer.repository.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.xtn.locktimer.model.Battery


@Dao
interface BatteryDao {

    @Query("SELECT * FROM battery ORDER BY id DESC")
    fun getBatteries() : LiveData<List<Battery>>

    @Query("SELECT * FROM battery WHERE percentage = :percentage")
    fun getBattery(percentage: Int) : Battery?

    @Insert
    fun insert(battery: Battery)

    @Update
    fun update(battery: Battery)

    @Delete
    fun delete(battery: Battery)

    @Query("DELETE FROM battery WHERE id = (SELECT id FROM battery LIMIT 1)")
    fun deleteTopRow()

}