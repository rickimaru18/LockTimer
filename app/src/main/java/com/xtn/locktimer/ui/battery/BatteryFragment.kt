package com.xtn.locktimer.ui.battery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xtn.locktimer.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_battery.*
import javax.inject.Inject


@AndroidEntryPoint
class BatteryFragment : Fragment() {

    @Inject lateinit var batteryViewModel: BatteryViewModel

    @Inject lateinit var batteryPresetsListAdapter: BatteryPresetsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
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
                batteryViewModel.setBattery(posToInt)
            }
            position = 0.0f
            startText ="${min}%"
            endText = "${max}%"
        }

        vrecycler_battery_presets.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = batteryPresetsListAdapter
        }
    }

    /**
     * Setup ViewModels of this fragment.
     */
    private fun setupViewModels() {
        batteryViewModel.apply {
            battery.observe(viewLifecycleOwner, Observer {
                vslider_battery_percentage.position = it.toFloat() / 100f
            })

            getBatteries().observe(viewLifecycleOwner, Observer {
                if (it.isEmpty()) return@Observer
                batteryPresetsListAdapter.setBatteries(it)
                vslider_battery_percentage.position = it.first().percentage.toFloat() / 100f
            })
        }
    }

}