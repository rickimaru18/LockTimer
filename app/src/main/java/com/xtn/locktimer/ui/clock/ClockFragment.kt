package com.xtn.locktimer.ui.clock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.xtn.locktimer.R
import kotlinx.android.synthetic.main.fragment_clock.*


class ClockFragment : Fragment() {

    private lateinit var _clockViewModel: ClockViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
            _clockViewModel.setHours(hourOfDay)
            _clockViewModel.setMinutes(minute)
        }
    }

    /**
     * Setup ViewModels of this fragment.
     */
    private fun setupViewModels() {
        _clockViewModel = ViewModelProvider(requireActivity()).get(ClockViewModel::class.java)

        _clockViewModel.getClock().observe(this, Observer {
            if (it == null) {
                return@Observer
            }

            _clockViewModel.setHours(it.hours)
            _clockViewModel.setMinutes(it.minutes)
            vtimepicker_clock.apply {
                hour = it?.hours!!
                minute = it?.minutes!!
            }
        })
    }

}