package com.example.passengerapp.ui.screens.passengerlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.passengerapp.databinding.ItemPassengerBinding
import com.example.passengerapp.ui.screens.passengerlist.list.PassengerItemView
import com.example.passengerapp.ui.screens.passengerlist.list.PassengerItemViewModel

class PassengerLayoutAdapter :
    PagingDataAdapter<PassengerItemViewModel, PassengerItemView>(
        PASSENGER_LAYOUT_COMPARATOR
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassengerItemView {
        val binding =
            ItemPassengerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PassengerItemView(binding) { position, expanded ->
            notifyItemChanged(position, expanded)
        }
    }

    override fun onBindViewHolder(holder: PassengerItemView, position: Int) {
        holder.also { passengerItemView ->
            getItem(position)?.also {
                passengerItemView.setViewModel(it)
                passengerItemView.bind(it.getLayout(),position)
            }
        }
    }

    override fun onBindViewHolder(
        holder: PassengerItemView,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            holder.onBind(payloads)
        }
    }

    companion object {
        private val PASSENGER_LAYOUT_COMPARATOR =
            object : DiffUtil.ItemCallback<PassengerItemViewModel>() {
                override fun areItemsTheSame(
                    oldItem: PassengerItemViewModel,
                    newItem: PassengerItemViewModel
                ): Boolean {
                    return oldItem.hashCode() == newItem.hashCode()
                }

                override fun areContentsTheSame(
                    oldItem: PassengerItemViewModel,
                    newItem: PassengerItemViewModel
                ): Boolean {
                    return oldItem.equals(newItem)
                }
            }
    }
}