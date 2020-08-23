package com.xtn.locktimer.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xtn.locktimer.util.Utils
import java.util.*


@Entity(tableName = "clock")
data class Clock(
    val hours: Int,
    val minutes: Int
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 1

    override fun toString(): String {
        val isJapanese = Utils.isLanguageJapanese()
        val sb = StringBuffer()
        var amPm = if (isJapanese) "午前" else "AM"

        if (hours in 0..11) {
            sb.append(
                if (hours == 0) 12
                else hours
            )
        } else {
            sb.append(
                if (hours > 12) hours - 12
                else hours
            )
            amPm = if (isJapanese) "午後" else "PM"
        }

        sb.append(":")

        if (minutes in 0..9) {
            sb.append(0).append(minutes)
        } else {
            sb.append(minutes)
        }

        if (isJapanese) {
            sb.insert(0, amPm)
        } else {
            sb.append(" ").append(amPm)
        }

        return sb.toString()
    }

}