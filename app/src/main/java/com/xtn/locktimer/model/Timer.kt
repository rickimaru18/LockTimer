package com.xtn.locktimer.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity(tableName = "timer")
data class Timer(
    val minutes: Long
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @Ignore
    override fun toString(): String {
        return StringBuffer().apply {
            append("ID: $id\n")
            append("Minutes: $minutes\n")
        }.toString()
    }

}