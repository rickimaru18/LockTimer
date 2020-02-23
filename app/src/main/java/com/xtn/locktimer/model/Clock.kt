package com.xtn.locktimer.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "clock")
data class Clock(
    val hours: Int,
    val minutes: Int
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 1

}