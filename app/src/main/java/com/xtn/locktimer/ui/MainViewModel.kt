package com.xtn.locktimer.ui

import android.app.Application
import androidx.lifecycle.*
import com.xtn.locktimer.model.Battery
import com.xtn.locktimer.model.Clock
import com.xtn.locktimer.model.LockTimerInfo
import com.xtn.locktimer.model.Timer
import com.xtn.locktimer.repository.RoomRepo
import com.xtn.locktimer.worker.WorkerUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MainViewModel @Inject constructor(
    application: Application,
    private val roomRepo: RoomRepo
) : AndroidViewModel(application) {

    private val _isAdminActive = MutableLiveData<Boolean>().apply { value = false }
    private val _isLockTimerStarted = MutableLiveData<Boolean>().apply { value = false }

    val isAdminActive: LiveData<Boolean> = _isAdminActive
    val isLockTimerStarted: LiveData<Boolean> = _isLockTimerStarted

    /**
     * Set value of [isAdminActive].
     *
     * @param isActive  TRUE if active, FALSE otherwise.
     */
    fun setAdminActive(isActive: Boolean) {
        _isAdminActive.value = isActive
    }

    /**
     * Set value of [isLockTimerStarted].
     *
     * @param isStarted TRUE if lock timer is started, FALSE otherwise.
     */
    fun setIsLockTimerStarted(isStarted: Boolean) {
        _isLockTimerStarted.value = isStarted
    }

    /**
     * Start lock timer.
     *
     * @param clock Clock instance in which the screen lock will execute.
     */
    fun startLockTimer(clock: Clock) {
        viewModelScope.launch(Dispatchers.IO) {
            roomRepo.update(LockTimerInfo(true, 0))
            roomRepo.update(clock)
        }
        WorkerUtil.scheduleScreenLockWorker(
            getApplication(),
            LocalTime.of(clock.hours, clock.minutes)
        )
    }

    /**
     * Start lock timer.
     *
     * @param minutes   Minutes in which the screen lock will execute.
     */
    fun startLockTimer(minutes: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            roomRepo.update(LockTimerInfo(true, 1))
            roomRepo.insertUnique(Timer(minutes)).let { isUnique ->
                if (!isUnique) roomRepo.deleteTopRow(Timer.TABLE_NAME)
            }
        }
        WorkerUtil.scheduleScreenLockWorker(getApplication(), minutes * 60)
    }

    /**
     * Start lock timer.
     *
     * @param battery   Battery percentage in which the screen lock will execute.
     */
    fun startLockTimer(battery: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            roomRepo.update(LockTimerInfo(true, 2))
            roomRepo.insertUnique(Battery(battery)).let { isUnique ->
                if (!isUnique) roomRepo.deleteTopRow(Battery.TABLE_NAME)
            }
        }
        WorkerUtil.scheduleScreenLockWorker(getApplication(), battery)
    }

    /**
     * Stop lock timer.
     */
    fun stopLockTimer() {
        viewModelScope.launch(Dispatchers.IO) {
            roomRepo.update(LockTimerInfo(false, -1))
        }
        WorkerUtil.stopScreenLockWorker(getApplication())
    }

    /**
     * Get [LockTimerInfo] data from database.
     *
     * @return [LockTimerInfo] data.
     */
    fun getLockTimerInfo() = liveData(Dispatchers.IO) {
        val res = roomRepo.getLockTimerInfo()
        emitSource(res)
    }

}