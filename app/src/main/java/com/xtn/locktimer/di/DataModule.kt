package com.xtn.locktimer.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.xtn.locktimer.model.Battery
import com.xtn.locktimer.model.Clock
import com.xtn.locktimer.model.LockTimerInfo
import com.xtn.locktimer.model.Timer
import com.xtn.locktimer.repository.RoomRepo
import com.xtn.locktimer.repository.db.LockTimerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun providesRoomRepo(@ApplicationContext context: Context) : RoomRepo {
        var roomRepo: RoomRepo? = null
        val db = Room.databaseBuilder(
            context,
            LockTimerDatabase::class.java,
            "lock_timer_db"
        ).addCallback(object: RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                GlobalScope.launch(Dispatchers.IO) {
                    roomRepo?.insert(LockTimerInfo(false, -1))
                    roomRepo?.insert(Clock(LocalTime.now().hour, LocalTime.now().minute))
                    roomRepo?.insert(Timer(20))
                    roomRepo?.insert(Timer(30))
                    roomRepo?.insert(Timer(50))
                    roomRepo?.insert(Timer(60))
                    roomRepo?.insert(Timer(90))
                    roomRepo?.insert(Battery(50))
                    roomRepo?.insert(Battery(40))
                    roomRepo?.insert(Battery(30))
                    roomRepo?.insert(Battery(20))
                    roomRepo?.insert(Battery(10))
                }
            }

        }).build()
        roomRepo = RoomRepo(db)

        return roomRepo
    }

}