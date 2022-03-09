package com.example.passengerapp.ui.screens.passengercreating


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.passengerapp.BR
import com.example.passengerapp.R
import com.example.passengerapp.databinding.ItemAirlineBinding
import com.example.passengerapp.model.Airline

class AirlineAdapter(lastSelectedPosition: Int? = null) :
    ListAdapter<Airline, RecyclerView.ViewHolder>(AirlineDiffCallback()) {

    var airlineRemovingListener: ((Int) -> Unit)? = null

    private var airlineChosenListener: ((Int?) -> Unit)? = null
    fun setOnChosenAirlineListener(listener: (Int?) -> Unit) {
        airlineChosenListener = listener
    }

    private val mapBinding: MutableMap<Int, ItemAirlineBinding> = mutableMapOf()
    private var previousPosition: Int?

    init {
        previousPosition = lastSelectedPosition
    }

    inner class AirlineViewHolder(val binding: ItemAirlineBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            setOnAirlineRemovingListener { toggleState(it) }
        }

        fun bind(airline: Airline, position: Int) {
            binding.setVariable(BR.airline, airline)
            if (previousPosition != null && previousPosition == position) {
                mapBinding[position] = binding
            }
            toggleBackgroundColor(binding, mapBinding[position] != null)
            binding.containerAirline.setOnClickListener {
                toggleState(position)
            }
        }

        private fun setOnAirlineRemovingListener(listener: (Int) -> Unit) {
            airlineRemovingListener = listener
        }

        private fun toggleState(position: Int) {
            val stateList = generateListStates(position)
            for (state in stateList) {
                when (state) {
                    AirlineItemState.SELECTED -> {
                        toggleBackgroundColor(binding, true)
                        mapBinding[position] = binding
                        previousPosition = position
                        airlineChosenListener?.let { click ->
                            click(position)
                        }
                    }
                    AirlineItemState.PREVIOUS_POSITION_SELECTED -> {
                        toggleBackgroundColor(mapBinding[previousPosition], false)
                        mapBinding.remove(previousPosition)
                    }
                    AirlineItemState.UNSELECTED -> {
                        toggleBackgroundColor(mapBinding[position], false)
                        mapBinding.remove(position)
                        airlineChosenListener?.let { click ->
                            click(null)
                        }
                        previousPosition = null
                    }
                }
            }
        }

        private fun generateListStates(position: Int): List<AirlineItemState> {
            val statesList = mutableListOf<AirlineItemState>()
            if (mapBinding[position] == null && previousPosition != null) {
                statesList.add(AirlineItemState.PREVIOUS_POSITION_SELECTED)
            }
            if (mapBinding[position] == null) {
                statesList.add(AirlineItemState.SELECTED)
            }
            if (mapBinding[position] != null) {
                statesList.add(AirlineItemState.UNSELECTED)
            }
            return statesList
        }

        private fun toggleBackgroundColor(binding: ItemAirlineBinding?, selected: Boolean) {
            binding?.containerAirline?.backgroundTintList =
                ContextCompat.getColorStateList(
                    itemView.context,
                    if (selected) R.color.purple_200 else R.color.grey
                )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemAirlineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AirlineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as AirlineViewHolder).bind(getItem(position), position)
    }

    class AirlineDiffCallback : DiffUtil.ItemCallback<Airline>() {

        override fun areItemsTheSame(oldItem: Airline, newItem: Airline): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Airline, newItem: Airline): Boolean =
            oldItem == newItem
    }

    enum class AirlineItemState {
        SELECTED, UNSELECTED, PREVIOUS_POSITION_SELECTED
    }
}