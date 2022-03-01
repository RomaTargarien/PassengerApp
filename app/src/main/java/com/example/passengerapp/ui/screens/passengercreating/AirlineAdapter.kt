package com.example.passengerapp.ui.screens.passengercreating


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.passengerapp.BR
import com.example.passengerapp.databinding.ItemAirlineBinding
import com.example.passengerapp.model.Airline

class AirlineAdapter : ListAdapter<Airline, RecyclerView.ViewHolder>(AirlineDiffCallback()) {

    inner class AirlineViewHolder(val binding: ItemAirlineBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(airline: Airline) {
            binding.setVariable(BR.airline,airline)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemAirlineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AirlineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as AirlineViewHolder).bind(getItem(position))
    }

    class AirlineDiffCallback : DiffUtil.ItemCallback<Airline>() {

        override fun areItemsTheSame(oldItem: Airline, newItem: Airline): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Airline, newItem: Airline): Boolean =
            oldItem == newItem
    }
}