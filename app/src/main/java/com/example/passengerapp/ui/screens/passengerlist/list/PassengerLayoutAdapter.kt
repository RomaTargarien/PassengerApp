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
        return PassengerItemView(binding)
    }

    override fun onBindViewHolder(holder: PassengerItemView, position: Int) {
        holder.also { passengerItemView ->
            getItem(position)?.also {
                passengerItemView.setViewModel(it)
            }
        }
    }

    override fun onViewAttachedToWindow(holder: PassengerItemView) {
        super.onViewAttachedToWindow(holder)
        holder.onViewAttached()
    }

    override fun onViewDetachedFromWindow(holder: PassengerItemView) {
        super.onViewDetachedFromWindow(holder)
        holder.onViewDetached()
    }

    override fun onViewRecycled(holder: PassengerItemView) {
        super.onViewRecycled(holder)
        holder.onViewRecycled()
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