package com.xtn.locktimer.ui.battery

import androidx.lifecycle.*
import com.xtn.locktimer.model.Battery
import com.xtn.locktimer.repository.RoomRepo
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class BatteryViewModel @Inject constructor(
    private val roomRepo: RoomRepo
) : ViewModel() {

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
        val res = roomRepo.getBatteries()
        emitSource(res)
    }

}