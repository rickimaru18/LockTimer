package com.xtn.locktimer.ui.clock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.xtn.locktimer.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_clock.*
import javax.inject.Inject


@AndroidEntryPoint
class ClockFragment : Fragment() {

    @Inject lateinit var clockViewModel: ClockViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        return inflater.inflate(R.layout.fragment_clock, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        setupViewModels()
    }

    /**
     * Setup views of this fragment.
     */
    private fun setupViews() {
        vtimepicker_clock.setOnTimeChangedListener { timePicker, hourOfDay, minute ->
            clockViewModel.setHours(hourOfDay)
            clockViewModel.setMinutes(minute)
        }
    }

    /**
     * Setup ViewModels of this fragment.
     */
    private fun setupViewModels() {
        clockViewModel.getClock().observe(viewLifecycleOwner, Observer {
            if (it == null) {
                return@Observer
            }

            clockViewModel.setHours(it.hours)
            clockViewModel.setMinutes(it.minutes)
            vtimepicker_clock.apply {
                hour = it?.hours!!
                minute = it?.minutes!!
            }
        })
    }

}