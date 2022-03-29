package com.example.passengerapp.ui.screens.passengerlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.passengerapp.R
import com.example.passengerapp.databinding.ItemPassengerBinding
import com.example.passengerapp.model.ui.PassengerLayout
import com.example.passengerapp.ui.util.extensions.rotate
import com.example.passengerapp.ui.util.extensions.scale
import com.example.passengerapp.ui.util.extensions.setHeight

class PassengerLayoutAdapter :
    PagingDataAdapter<PassengerLayout, PassengerLayoutAdapter.PassengerLayoutViewHolder>(
        PASSENGER_LAYOUT_COMPARATOR
    ) {

    private var onExpandedClickListener: ((Pair<PassengerLayout, Int>) -> Unit)? = null

    fun getPassengerLayout(position: Int) = getItem(position)

    fun setOnExpandedClickListener(listener: (Pair<PassengerLayout, Int>) -> Unit) {
        onExpandedClickListener = listener
    }

    inner class PassengerLayoutViewHolder(val binding: ItemPassengerBinding) : ViewHolder(binding.root) {

        private var containerHeight: Int =
            itemView.resources.getDimension(R.dimen.passenger_container_height).toInt()

        private lateinit var passengerLayout: PassengerLayout
        private var passengerLayoutPosition = -1

        fun bind(passengerLayout: PassengerLayout, position: Int) {
            this.passengerLayout = passengerLayout
            this.passengerLayoutPosition = absoluteAdapterPosition
            binding.setVariable(BR.passenger, passengerLayout)
            val height = if (passengerLayout.selected) containerHeight else containerHeight / 2
            binding.passengerContainer.setHeight(height)
            binding.ivExpandAirlines.setOnClickListener {
                onExpandedClickListener?.invoke(Pair(passengerLayout, passengerLayoutPosition))
            }
        }

        fun onBind(passengerLayout: PassengerLayout, payloads: List<Any>) {
            this.passengerLayout = passengerLayout
            this.passengerLayoutPosition = bindingAdapterPosition
            val isSelected = payloads.last() as Boolean
            val from = if (isSelected) containerHeight / 2 else containerHeight
            val to = if (isSelected) containerHeight else containerHeight / 2
            binding.ivExpandAirlines.rotate(if (isSelected) 180f else 360f, ANIMATION_TIME)
            binding.passengerContainer.scale(from, to, ANIMATION_TIME)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassengerLayoutViewHolder {
        val binding =
            ItemPassengerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PassengerLayoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PassengerLayoutViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, position) }
    }

    override fun onBindViewHolder(
        holder: PassengerLayoutViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            holder.onBind(getItem(position)!!, payloads)
        }
    }

    companion object {
        private const val ANIMATION_TIME = 200L

        private val PASSENGER_LAYOUT_COMPARATOR =
            object : DiffUtil.ItemCallback<PassengerLayout>() {
                override fun areItemsTheSame(
                    oldItem: PassengerLayout,
                    newItem: PassengerLayout
                ): Boolean {
                    return oldItem.id == newItem.id && oldItem.name == newItem.name && newItem.trips == oldItem.trips
                }

                override fun areContentsTheSame(
                    oldItem: PassengerLayout,
                    newItem: PassengerLayout
                ): Boolean {
                    return oldItem.id == newItem.id
                }
            }
    }
}