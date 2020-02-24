package com.xtn.locktimer.ui.battery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xtn.locktimer.R
import com.xtn.locktimer.model.Battery
import kotlinx.android.synthetic.main.rowitem_presets.view.*


class BatteryPresetsListAdapter(
    private val _viewModelStoreOwner: ViewModelStoreOwner
) : RecyclerView.Adapter<BatteryPresetsListAdapter.BatteryPresetsListViewHolder>() {

    private var _batteries = listOf<Battery>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BatteryPresetsListViewHolder {
        val rowItemLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.rowitem_presets, parent, false)
        return BatteryPresetsListViewHolder(rowItemLayout)
    }

    override fun onBindViewHolder(holder: BatteryPresetsListViewHolder, position: Int) {
        val rowItem = holder.itemView
        val battery = _batteries[position]

        rowItem.vtxt_minutes.text = "${battery.percentage}%"
        rowItem.tag = battery.percentage

        if (!rowItem.hasOnClickListeners()) {
            rowItem.setOnClickListener {
                val batteryViewModel = ViewModelProvider(_viewModelStoreOwner).get(BatteryViewModel::class.java)
                batteryViewModel.setBattery(it.tag as Int)
            }
        }
    }

    override fun getItemCount() = _batteries.size

    /**
     * Set list of [Battery] to this adapter.
     *
     * @param batteries List of [Battery].
     */
    fun setBatteries(batteries: List<Battery>) {
        val diffResult = DiffUtil.calculateDiff(
            BatteryPresetsListDiffCallback(_batteries, batteries),
            true
        )
        _batteries = batteries
        diffResult.dispatchUpdatesTo(this)
    }


    /**
     *
     */
    class BatteryPresetsListViewHolder(rowItemView: View) : RecyclerView.ViewHolder(rowItemView)

    /**
     *
     */
    class BatteryPresetsListDiffCallback(
        private val mOldList: List<Battery>,
        private val mNewList: List<Battery>
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