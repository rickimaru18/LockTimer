package com.xtn.locktimer.ui.clock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.xtn.locktimer.model.Clock
import com.xtn.locktimer.repository.RoomRepo
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ClockViewModel @Inject constructor(
    private val roomRepo: RoomRepo
) : ViewModel() {

    private val _hours = MutableLiveData<Int>().apply { value = 0 }
    private val _minutes = MutableLiveData<Int>().apply { value = 0 }

    val hours: LiveData<Int> = _hours
    val minutes: LiveData<Int> = _minutes

    /**
     * Set [hours] value.
     *
     * @param hours Hours.
     */
    fun setHours(hours: Int) {
        if (_hours.value!! == hours) return
        _hours.value = hours
    }

    /**
     * Set [minutes] value.
     *
     * @param minutes Minutes.
     */
    fun setMinutes(minutes: Int) {
        if (_minutes.value!! == minutes) return
        _minutes.value = minutes
    }

    /**
     * Create a [Clock] based from [hours] and [minutes].
     *
     * @return [Clock] instance.
     */
    fun convertHoursAndMinutesToClock() = Clock(hours.value!!, minutes.value!!)

    /**
     * Get [Clock] data from database.
     *
     * @return [Clock] data.
     */
    fun getClock() = liveData(Dispatchers.IO) {
        val res = roomRepo.getClock()
        emitSource(res)
    }

}