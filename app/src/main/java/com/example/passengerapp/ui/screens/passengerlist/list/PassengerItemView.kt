package com.example.passengerapp.ui.screens.passengerlist.list

import androidx.recyclerview.widget.RecyclerView
import com.example.passengerapp.R
import com.example.passengerapp.databinding.ItemPassengerBinding
import com.example.passengerapp.model.ui.PassengerLayout
import com.example.passengerapp.ui.util.extensions.rotate
import com.example.passengerapp.ui.util.extensions.scale
import com.example.passengerapp.ui.util.extensions.setHeight

class PassengerItemView(
    val binding: ItemPassengerBinding,
    private val notifyAdapterExpandItem: (Int, Boolean) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var containerHeight: Int =
        itemView.resources.getDimension(R.dimen.passenger_container_height).toInt()
    private var passengerItemPosition: Int = -1
    private lateinit var viewModel: PassengerItemViewModel

    init {
        binding.ivExpandAirlines.setOnClickListener {
            viewModel.changeExpandedState(!viewModel.passengerLayout.expanded)
            notifyAdapterExpandItem.invoke(
                passengerItemPosition,
                viewModel.passengerLayout.expanded
            )
        }
    }

    fun bind(passengerLayout: PassengerLayout, position: Int) {
        passengerItemPosition = position
        val height = if (passengerLayout.expanded) containerHeight else containerHeight / 2
        binding.passengerContainer.setHeight(height)
    }

    fun onBind(payloads: List<Any>) {
        val isSelected = payloads.last() as Boolean
        val from = if (isSelected) containerHeight / 2 else containerHeight
        val to = if (isSelected) containerHeight else containerHeight / 2
        binding.ivExpandAirlines.rotate(if (isSelected) 180f else 360f, ANIMATION_TIME)
        binding.passengerContainer.scale(from, to, ANIMATION_TIME)
    }

    fun setViewModel(passengerItemViewModel: PassengerItemViewModel) {
        viewModel = passengerItemViewModel
        binding.passenger = viewModel.getLayout()
    }

    companion object {
        private const val ANIMATION_TIME = 300L
    }
}