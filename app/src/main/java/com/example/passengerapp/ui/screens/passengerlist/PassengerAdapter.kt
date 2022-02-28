package com.example.passengerapp.ui.screens.passengerlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.children
import androidx.databinding.library.baseAdapters.BR
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.passengerapp.R
import com.example.passengerapp.databinding.ItemPassengerBinding
import com.example.passengerapp.model.Passenger

class PassengerAdapter :
    PagingDataAdapter<Passenger, PassengerAdapter.PassengerViewHolder>(PASSENGER_COMPARATOR) {

    private var onDeleteListener: ((Passenger) -> Unit)? = null
    fun setOnDeleteListener(listener: (Passenger) -> Unit) {
        onDeleteListener = listener
    }

    private val mapBinding: MutableMap<Int, ItemPassengerBinding> = mutableMapOf()

    fun getPassenger(position: Int) = getItem(position)

    inner class PassengerViewHolder(val binding: ItemPassengerBinding) : ViewHolder(binding.root) {
        fun bind(passenger: Passenger, position: Int) {
            binding.setVariable(BR.passenger, passenger)
            if (mapBinding[position] != null) {
                handleExpandedState(true)
            } else {
                handleExpandedState(false)
            }
            binding.ivExpandAirlines.setOnClickListener {
                if (mapBinding[position] == null) {
                    mapBinding[position] = binding
                    handleExpandAirlineDetailsClick(true)
                } else {
                    mapBinding.remove(position)
                    handleExpandAirlineDetailsClick(false)
                }
            }
        }

        private fun handleExpandedState(isExpanded: Boolean) {
            binding.ivExpandAirlines.rotation = if (isExpanded) 180f else 360f
            binding.airLineDetailsContainer.children.forEach {
                it.alpha = if (isExpanded) 1f else 0f
            }
            binding.constraintLayout
                .transitionToState(if (isExpanded) R.id.end else R.id.start)
        }

        private fun handleExpandAirlineDetailsClick(isExpanded: Boolean) {
            val alpha = if (isExpanded) 1f else 0f
            binding.ivExpandAirlines
                .animate()
                .rotation(if (isExpanded) 180f else 360f)
                .setDuration(ANIMATION_TIME)
                .start()
            binding.airLineDetailsContainer.children.forEach {
                it.animate()
                    .alpha(alpha)
                    .setDuration(ANIMATION_TIME)
                    .start()
            }
            binding.constraintLayout
                .transitionToState(if (isExpanded) R.id.end else R.id.start)
        }
    }


    override fun onBindViewHolder(holder: PassengerViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, position) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassengerViewHolder {
        val binding =
            ItemPassengerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PassengerViewHolder(binding)
    }

    companion object {
        private const val ANIMATION_TIME = 200L

        private val PASSENGER_COMPARATOR = object : DiffUtil.ItemCallback<Passenger>() {
            override fun areItemsTheSame(oldItem: Passenger, newItem: Passenger): Boolean {
                return oldItem.id == newItem.id && oldItem.name == newItem.name && newItem.trips == oldItem.trips
            }

            override fun areContentsTheSame(oldItem: Passenger, newItem: Passenger): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}