package com.example.passengerapp.ui.screens.passengerlist.list

import androidx.recyclerview.widget.RecyclerView
import com.example.passengerapp.R
import com.example.passengerapp.databinding.ItemPassengerBinding
import com.example.passengerapp.ui.util.extensions.rotate
import com.example.passengerapp.ui.util.extensions.scale
import com.example.passengerapp.ui.util.extensions.setHeight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class PassengerItemView(
    val binding: ItemPassengerBinding
) : RecyclerView.ViewHolder(binding.root) {

    private var containerHeight: Int =
        itemView.resources.getDimension(R.dimen.passenger_container_height).toInt()
    private lateinit var viewModel: PassengerItemViewModel
    private var passengerItemViewScope: CoroutineScope? = null

    fun onViewAttached() {
        viewModel.layoutIsVisible = true
    }

    fun onViewDetached() {
        viewModel.layoutIsVisible = false
    }

    fun onViewRecycled() {
        passengerItemViewScope?.cancel()
        passengerItemViewScope = null
    }

    fun setViewModel(passengerItemViewModel: PassengerItemViewModel) {
        viewModel = passengerItemViewModel
        passengerItemViewScope = CoroutineScope(Dispatchers.Main)
        passengerItemViewScope?.launch {
            viewModel.passengerLayoutFlow.collect {
                binding.passenger = it
                if (it.startExpandAnimation) {
                    startAnimation(it.expanded)
                } else {
                    val height = if (it.expanded) containerHeight else containerHeight / 2
                    binding.passengerContainer.setHeight(height)
                    binding.ivExpandAirlines.rotation = if (it.expanded) 180f else 360f
                }
                binding.ivExpandAirlines.setOnClickListener {
                    viewModel.onExpandClickStartAnimation()
                }
            }
        }
    }

    private fun startAnimation(isExpanded: Boolean) {
        val from = if (isExpanded) containerHeight / 2 else containerHeight
        val to = if (isExpanded) containerHeight else containerHeight / 2
        binding.ivExpandAirlines.rotate(
            degree = if (isExpanded) 180f else 360f,
            duration = ANIMATION_TIME
        )
        binding.passengerContainer.scale(
            from = from,
            to = to,
            duration = ANIMATION_TIME,
            onAnimationEnd = {
                viewModel.onExpandedAnimationEnded()
            }
        )
    }

    companion object {
        private const val ANIMATION_TIME = 300L
    }
}