package com.xtn.locktimer.ui.timer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xtn.locktimer.R
import com.xtn.locktimer.model.Timer
import kotlinx.android.synthetic.main.rowitem_presets.view.*


class TimerPresetsListAdapter(
    private val _viewModelStoreOwner: ViewModelStoreOwner
) : RecyclerView.Adapter<TimerPresetsListAdapter.TimerPresetsListViewHolder>() {

    private var _timers = listOf<Timer>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerPresetsListViewHolder {
        val rowItemLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.rowitem_presets, parent, false)
        return TimerPresetsListViewHolder(rowItemLayout)
    }

    override fun onBindViewHolder(holder: TimerPresetsListViewHolder, position: Int) {
        val rowItem = holder.itemView
        val timer = _timers[position]

        rowItem.vtxt_minutes.text = "${timer.minutes} min(s)"
        rowItem.tag = timer.minutes

        if (!rowItem.hasOnClickListeners()) {
            rowItem.setOnClickListener {
                val timerViewModel = ViewModelProvider(_viewModelStoreOwner).get(TimerViewModel::class.java)
                timerViewModel.setMinutes(it.tag as Long)
            }
        }
    }

    override fun getItemCount() = _timers.size

    /**
     * Set list of [Timer] to this adapter.
     *
     * @param timers List of [Timer].
     */
    fun setTimers(timers: List<Timer>) {
        val diffResult = DiffUtil.calculateDiff(
            TimerPresetsListDiffCallback(_timers, timers),
            true
        )
        _timers = timers
        diffResult.dispatchUpdatesTo(this)
    }


    /**
     *
     */
    class TimerPresetsListViewHolder(rowItemView: View) : RecyclerView.ViewHolder(rowItemView)

    /**
     *
     */
    class TimerPresetsListDiffCallback(
        private val mOldList: List<Timer>,
        private val mNewList: List<Timer>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = mOldList.size

        override fun getNewListSize() = mNewList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldList[oldItemPosition].id == mNewList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldList[oldItemPosition] equals mNewList[newItemPosition]
        }

    }

}