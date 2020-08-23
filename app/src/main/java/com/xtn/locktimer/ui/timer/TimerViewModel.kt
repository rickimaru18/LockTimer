package com.xtn.locktimer.ui.timer

import androidx.lifecycle.*
import com.xtn.locktimer.model.Timer
import com.xtn.locktimer.repository.RoomRepo
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TimerViewModel @Inject constructor(
    private val roomRepo: RoomRepo
) : ViewModel() {

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
        val res = roomRepo.getTimers()
        emitSource(res)
    }

}