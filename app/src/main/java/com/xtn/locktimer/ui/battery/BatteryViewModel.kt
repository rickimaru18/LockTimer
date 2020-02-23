package com.xtn.locktimer.ui.battery

import android.app.Application
import androidx.lifecycle.*
import com.xtn.locktimer.model.Battery
import com.xtn.locktimer.repository.RoomRepo
import kotlinx.coroutines.Dispatchers


class BatteryViewModel(application: Application) : AndroidViewModel(application) {

    private val _roomRepo = RoomRepo.getInstance(application)

    private val _battery = MutableLiveData<Int>().apply { value = 1 }

    val battery: LiveData<Int> = _battery

    /**
     * Set [battery] value.
     *
     * @param value Battery.
     */
    fun setBattery(value: Int) {
        if (_battery.value == value) return
        _battery.value = value
    }

    /**
     * Get list of [Battery] data from database.
     *
     * @return List of [Battery].
     */
    fun getBatteries() = liveData(Dispatchers.IO) {
        val res = _roomRepo.getBatteries()
        emitSource(res)
    }

}