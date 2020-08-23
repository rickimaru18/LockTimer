package com.xtn.locktimer.ui.timer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xtn.locktimer.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_timer.*
import javax.inject.Inject


@AndroidEntryPoint
class TimerFragment : Fragment() {

    @Inject lateinit var timerViewModel: TimerViewModel

    @Inject lateinit var timerPresetsListAdapter: TimerPresetsListAdapter

    private var _isSetMinutesText = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
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
                timerViewModel.setMinutes(text.toString().toLong())
            }
        )

        vrecycler_timer_presets.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = timerPresetsListAdapter
        }
    }

    /**
     * Setup ViewModels of this fragment.
     */
    private fun setupViewModels() {
        timerViewModel.apply {
            minutes.observe(viewLifecycleOwner, Observer {
                if (it > 0 && _isSetMinutesText) {
                    vedit_minutes.setText(it.toString())
                }
                _isSetMinutesText = true
            })

            getTimers().observe(viewLifecycleOwner, Observer {
                if (it.isEmpty()) return@Observer
                timerPresetsListAdapter.setTimers(it)
                setMinutes(it.first().minutes)
            })
        }
    }

}