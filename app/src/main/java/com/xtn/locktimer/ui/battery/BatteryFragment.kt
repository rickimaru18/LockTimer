package com.xtn.locktimer.ui.battery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.xtn.locktimer.R
import kotlinx.android.synthetic.main.fragment_battery.*


class BatteryFragment : Fragment() {

    private var _batteryViewModel: BatteryViewModel? = null
    private lateinit var _batteryPresetsListAdapter: BatteryPresetsListAdapter

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

        _batteryPresetsListAdapter = BatteryPresetsListAdapter(requireActivity())
        vrecycler_battery_presets.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = _batteryPresetsListAdapter
        }
    }

    /**
     * Setup ViewModels of this fragment.
     */
    private fun setupViewModels() {
        _batteryViewModel = ViewModelProvider(requireActivity()).get(BatteryViewModel::class.java)

        _batteryViewModel!!.apply {
            battery.observe(this@BatteryFragment, Observer {
                vslider_battery_percentage.position = it.toFloat() / 100f
            })

            getBatteries().observe(this@BatteryFragment, Observer {
                if (it.isEmpty()) return@Observer
                _batteryPresetsListAdapter.setBatteries(it)
                vslider_battery_percentage.position = it.first().percentage.toFloat() / 100f
            })
        }
    }

}