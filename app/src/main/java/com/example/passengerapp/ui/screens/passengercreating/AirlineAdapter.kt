package com.example.passengerapp.ui.screens.passengercreating


import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.example.passengerapp.BR
import com.example.passengerapp.databinding.ItemAirlineBinding
import com.example.passengerapp.model.ui.AirlineLayout

class AirlineAdapter : ListAdapter<AirlineLayout, RecyclerView.ViewHolder>(AirlineDiffCalBack()) {


    private var onAirlineSelectedListener: ((AirlineLayout) -> Unit)? = null

    fun setOnAirlineSelectedListener(listener: ((AirlineLayout) -> Unit)) {
        onAirlineSelectedListener = listener
    }

    inner class AirlineViewHolder(val binding: ItemAirlineBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var item: AirlineLayout

        init {
            binding.bnChoose.setOnClickListener {
                onAirlineSelectedListener?.invoke(item)
            }
        }

        fun onBind(airlineLayout: AirlineLayout) {
            this.item = airlineLayout
            binding.setVariable(BR.airline, airlineLayout)
        }

        fun onBind(airlineLayout: AirlineLayout, payloads: List<Any>) {
            this.item = airlineLayout
            val isSelected = payloads.last() as Boolean
            binding.bnChoose.text = if (isSelected) "Deselect" else "Choose"
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemAirlineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AirlineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as AirlineViewHolder).onBind(currentList[position])
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            (holder as AirlineViewHolder).onBind(currentList[position], payloads)
        }
    }

    private class AirlineDiffCalBack : DiffUtil.ItemCallback<AirlineLayout>() {
        override fun areItemsTheSame(oldItem: AirlineLayout, newItem: AirlineLayout) =
            oldItem.uniqueID == newItem.uniqueID

        override fun areContentsTheSame(oldItem: AirlineLayout, newItem: AirlineLayout) =
            oldItem == newItem

        override fun getChangePayload(oldItem: AirlineLayout, newItem: AirlineLayout): Any? {
            if (oldItem.selected != newItem.selected) return newItem.selected
            return super.getChangePayload(oldItem, newItem)
        }
    }
}