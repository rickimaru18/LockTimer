package com.xtn.locktimer.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "battery")
data class Battery(
    val percentage: Int
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

}