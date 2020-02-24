package com.xtn.locktimer.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity(tableName = "battery")
data class Battery(
    val percentage: Int
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    companion object {
        @Ignore
        val TABLE_NAME = "battery"
    }

    @Ignore
    infix fun equals(other: Battery) : Boolean {
        return percentage == other.percentage
    }

}