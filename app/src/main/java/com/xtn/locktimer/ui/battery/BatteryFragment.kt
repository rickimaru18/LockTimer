package com.xtn.locktimer.ui.battery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.xtn.locktimer.R
import kotlinx.android.synthetic.main.fragment_battery.*


class BatteryFragment : Fragment() {

    private var _batteryViewModel: BatteryViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_battery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        setupViewModels()
    }

    /**
     * Setup views of this fragment.
     */
    private fun setupViews() {
        vslider_battery_percentage.apply {
            val max = 100
            val min = 1
            val total = max - min

            positionListener = { pos ->
                val posToInt = min + (total  * pos).toInt()
                bubbleText = "${posToInt}%"
                _batteryViewModel?.setBattery(posToInt)
            }
            position = 0.0f
            startText ="${min}%"
            endText = "${max}%"
        }
    }

    /**
     * Setup ViewModels of this fragment.
     */
    private fun setupViewModels() {
        _batteryViewModel = ViewModelProvider(requireActivity()).get(BatteryViewModel::class.java)

        _batteryViewModel!!.apply {
            getBatteries().observe(this@BatteryFragment, Observer {
                if (it.isEmpty()) return@Observer
                vslider_battery_percentage.position = it.last().percentage.toFloat() / 100f
            })
        }
    }

}