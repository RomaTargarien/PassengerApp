package com.example.passengerapp.ui.screens.passengerlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.passengerapp.R
import com.example.passengerapp.databinding.ItemPassengerBinding
import com.example.passengerapp.model.Passenger
import com.squareup.picasso.Picasso

class PassengerAdapter :
    PagingDataAdapter<Passenger, PassengerAdapter.PassengerViewHolder>(PASSENGER_COMPARATOR) {

    inner class PassengerViewHolder(val binding: ItemPassengerBinding) : ViewHolder(binding.root) {
        fun bind(passenger: Passenger) {
            binding.tvName.text = passenger.name
            binding.tvTrips.text = passenger.trips.toString()
            var flag = false
            Picasso.get().load(passenger.airline[0].logo).into(binding.ivAirlineUrl)
            binding.ivExpandAirlines.setOnClickListener {
                if (flag) {
                    binding.ivExpandAirlines.animate().rotation(360f).setDuration(200).start()
                    binding.ivAirline.animate().alpha(0f).setDuration(200).start()
                    binding.constraintLayout.transitionToState(R.id.start)
                    flag = false
                } else {
                    binding.ivExpandAirlines.animate().rotation(180f).setDuration(200).start()
                    binding.ivAirline.animate().alpha(1f).setDuration(200).start()
                    binding.constraintLayout.transitionToState(R.id.end)
                    flag = true
                }
            }
        }
    }

    override fun onBindViewHolder(holder: PassengerViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassengerViewHolder {
        val binding =
            ItemPassengerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PassengerViewHolder(binding)
    }

    companion object {
        private val PASSENGER_COMPARATOR = object : DiffUtil.ItemCallback<Passenger>() {
            override fun areItemsTheSame(oldItem: Passenger, newItem: Passenger): Boolean {
                return oldItem._id == newItem._id && oldItem.name == newItem.name && newItem.trips == oldItem.trips
            }

            override fun areContentsTheSame(oldItem: Passenger, newItem: Passenger): Boolean {
                return oldItem._id == newItem._id
            }
        }
    }
}