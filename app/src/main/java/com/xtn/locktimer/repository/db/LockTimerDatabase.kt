package com.xtn.locktimer.repository.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.xtn.locktimer.model.Battery
import com.xtn.locktimer.model.Clock
import com.xtn.locktimer.model.LockTimerInfo
import com.xtn.locktimer.model.Timer
import com.xtn.locktimer.repository.db.dao.BatteryDao
import com.xtn.locktimer.repository.db.dao.ClockDao
import com.xtn.locktimer.repository.db.dao.LockTimerInfoDao
import com.xtn.locktimer.repository.db.dao.TimerDao


@Database(
    entities = [
        LockTimerInfo::class,
        Clock::class,
        Timer::class,
        Battery::class
    ],
    version = 1
)
abstract class LockTimerDatabase : RoomDatabase() {

    companion object {
        private var mInstance: LockTimerDatabase? = null

        /**
         * Get singleton instance.
         *
         * @return Singleton instance.
         */
        @Synchronized
        fun getInstance(context: Context) : LockTimerDatabase {
            if (mInstance == null) {
                mInstance = Room.databaseBuilder(
                    context.applicationContext,
                    LockTimerDatabase::class.java,
                    "lock_timer_db"
                ).build()
            }

            return mInstance!!
        }
    }

    /**
     * Get instance of [LockTimerInfoDao]
     *
     * @return [LockTimerInfoDao] instance.
     */
    abstract fun lockTimerInfoDao() : LockTimerInfoDao

    /**
     * Get instance of [ClockDao]
     *
     * @return [ClockDao] instance.
     */
    abstract fun clockDao() : ClockDao

    /**
     * Get instance of [TimerDao]
     *
     * @return [TimerDao] instance.
     */
    abstract fun timerDao() : TimerDao

    /**
     * Get instance of [BatteryDao]
     *
     * @return [BatteryDao] instance.
     */
    abstract fun batteryDao() : BatteryDao

}
