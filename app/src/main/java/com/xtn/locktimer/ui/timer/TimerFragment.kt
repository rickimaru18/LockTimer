package com.xtn.locktimer.ui.timer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.xtn.locktimer.R
import kotlinx.android.synthetic.main.fragment_timer.*


class TimerFragment : Fragment() {

    private lateinit var _timerViewModel: TimerViewModel

    private var _isSetMinutesText = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        setupViewModels()
    }

    /**
     * Setup views of this fragment.
     */
    private fun setupViews() {
        vedit_minutes.addTextChangedListener(
            { text, start, count, after ->  },
            { text, start, count, after ->  },
            { text ->
                if (text?.isBlank()!!) return@addTextChangedListener

                _isSetMinutesText = false
                _timerViewModel.setMinutes(text.toString().toLong())
            }
        )
    }

    /**
     * Setup ViewModels of this fragment.
     */
    private fun setupViewModels() {
        _timerViewModel = ViewModelProvider(requireActivity()).get(TimerViewModel::class.java)

        _timerViewModel.apply {
            minutes.observe(this@TimerFragment, Observer {
                if (it > 0 && _isSetMinutesText) {
                    vedit_minutes.setText(it.toString())
                }
                _isSetMinutesText = true
            })

            getTimers().observe(this@TimerFragment, Observer {
                if (it.isEmpty()) {
                    return@Observer
                }

                setMinutes(it.last().minutes)
            })
        }
    }

}