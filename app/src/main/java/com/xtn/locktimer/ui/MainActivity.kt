package com.xtn.locktimer.ui

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.*
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.xtn.locktimer.R
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.xtn.locktimer.admin.DeviceAdmin
import com.xtn.locktimer.ui.battery.BatteryViewModel
import com.xtn.locktimer.ui.clock.ClockViewModel
import com.xtn.locktimer.ui.timer.TimerViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val REQUEST_ADMIN_ENABLE = 0

    private lateinit var _mainViewModel: MainViewModel
    private lateinit var _clockViewModel: ClockViewModel
    private lateinit var _timerViewModel: TimerViewModel
    private lateinit var _batteryViewModel: BatteryViewModel

    private var _pendingEvent: ((data: Any) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViews()
        setupViewModels()
        requestAdminPolicy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    /**
     * Setup views of this activity.
     */
    private fun setupViews() {
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_clock,
                R.id.navigation_timer,
                R.id.navigation_battery
            )
        )
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            navController.popBackStack()
        }
        setupActionBarWithNavController(navController, appBarConfiguration)
        nav_view.setupWithNavController(navController)
    }

    /**
     * Setup ViewModels of this activity.
     */
    private fun setupViewModels() {
        _mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        _clockViewModel = ViewModelProvider(this).get(ClockViewModel::class.java)
        _timerViewModel = ViewModelProvider(this).get(TimerViewModel::class.java)
        _batteryViewModel = ViewModelProvider(this).get(BatteryViewModel::class.java)

        _mainViewModel.apply {
            isAdminActive.observe(this@MainActivity, Observer {
                if (it && _pendingEvent != null) {
                    _pendingEvent?.invoke(isLockTimerStarted.value!!)
                }
            })

            isLockTimerStarted.observe(this@MainActivity, Observer {
                if (fab_start_stop.tag == null) {
                    fab_start_stop.setOnClickListener {
                        startStopScreenLockTimer(fab_start_stop.tag as Boolean)
                    }
                }

                fab_start_stop.tag = it
                fab_start_stop.setImageResource(
                    if (it) R.drawable.ic_stop_24dp
                    else R.drawable.ic_play_24dp
                )
            })

            getLockTimerInfo().observe(this@MainActivity, Observer {
                if (it == null) return@Observer
                setIsLockTimerStarted(it.isLockTimerStarted)
            })
        }
    }

    /**
     * Setup and request for admin policies.
     */
    private fun requestAdminPolicy() {
        val devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val deviceAdmin = ComponentName(this, DeviceAdmin::class.java)

        if (devicePolicyManager.isAdminActive(deviceAdmin)) {
            _mainViewModel.setAdminActive(true)
        } else {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN , deviceAdmin)
                putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "This is needed to enable screen lock feature.")
            }
            startActivityForResult(
                intent,
                REQUEST_ADMIN_ENABLE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === REQUEST_ADMIN_ENABLE) {
            if (resultCode === Activity.RESULT_OK) {
                _mainViewModel.setAdminActive(true)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.vmenu_about -> showAboutDialog()

            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    /**
     * Start/Stop screen lock timer.
     *
     * @param isStarted     TRUE if you want to start the screen lock timer, FALSE otherwise.
     */
    private fun startStopScreenLockTimer(isStarted: Boolean) {
        if (_mainViewModel.isAdminActive.value == false) {
            requestAdminPolicy()
            _pendingEvent = ::startStopScreenLockTimer as (data: Any) -> Unit
            return
        }

        if (isStarted) {
            _mainViewModel.stopLockTimer()
        } else {
            var toastMsg = ""

            when (nav_view.selectedItemId) {
                R.id.navigation_clock -> {
                    _mainViewModel.startLockTimer(_clockViewModel.convertHoursAndMinutesToClock())
                    toastMsg = "Locking in ${_clockViewModel.hours.value}:${_clockViewModel.minutes.value}"
                }
                R.id.navigation_timer -> {
                    _mainViewModel.startLockTimer(_timerViewModel.minutes.value!!)
                    toastMsg = "Locking in ${_timerViewModel.minutes.value} minute(s)"
                }
                R.id.navigation_battery -> {
                    _mainViewModel.startLockTimer(_batteryViewModel.battery.value!!)
                    toastMsg = "Locking when battery is ${_batteryViewModel.battery.value!!}%"
                }
            }

            Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Show "About" dialog.
     */
    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setView(R.layout.dialog_about)
            .create()
            .show()
    }

}
