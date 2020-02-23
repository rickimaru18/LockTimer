package com.xtn.locktimer.ui.timer

import android.app.Application
import androidx.lifecycle.*
import com.xtn.locktimer.model.Timer
import com.xtn.locktimer.repository.RoomRepo
import kotlinx.coroutines.Dispatchers


class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val _roomRepo = RoomRepo.getInstance(application)

    private val _minutes = MutableLiveData<Long>().apply { value = 0 }

    val minutes: LiveData<Long> = _minutes

    /**
     * Set [minutes] value.
     *
     * @param value Minutes.
     */
    fun setMinutes(value: Long) {
        if (_minutes.value == value) return
        _minutes.value = value
    }

    /**
     * Get list of [Timer] data from database.
     *
     * @return List of [Timer].
     */
    fun getTimers() = liveData(Dispatchers.IO) {
        val res = _roomRepo.getTimers()
        emitSource(res)
    }

}