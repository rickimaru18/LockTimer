package com.xtn.locktimer.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity(tableName = "lock_timer_info")
data class LockTimerInfo(
    var isLockTimerStarted: Boolean,
    var type: Int
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 1

    @Ignore
    override fun toString(): String {
        return StringBuffer().apply {
            append("ID: $id\n")
            append("Is lock timer started: $isLockTimerStarted\n")
            append("Lock type: $type\n")
        }.toString()
    }

}