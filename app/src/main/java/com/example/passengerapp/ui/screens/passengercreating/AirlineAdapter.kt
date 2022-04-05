package com.example.passengerapp.ui.screens.passengercreating


import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.passengerapp.R
import com.example.passengerapp.databinding.ItemAirlineBinding
import com.example.passengerapp.model.ui.AirlineLayout
import com.example.passengerapp.ui.util.extensions.animateColorTransition

@RequiresApi(Build.VERSION_CODES.M)
class AirlineAdapter :
    ListAdapter<AirlineLayout, AirlineAdapter.AirlineViewHolder>(AirlineDiffCalBack()) {

    private var onAirlineSelectedListener: ((AirlineLayout) -> Unit)? = null

    fun setOnAirlineSelectedListener(listener: ((AirlineLayout) -> Unit)) {
        onAirlineSelectedListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AirlineViewHolder {
        val binding = ItemAirlineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AirlineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AirlineViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    override fun onBindViewHolder(
        holder: AirlineViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            holder.onBind(currentList[position], payloads)
        }
    }

    inner class AirlineViewHolder(val binding: ItemAirlineBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var item: AirlineLayout
        private var endContainerColor: Int
        private var endColorButton: Int
        private var startContainerColor: Int
        private var startColorButton: Int

        init {
            binding.bnChoose.setOnClickListener {
                onAirlineSelectedListener?.invoke(item)
            }
            startContainerColor = ContextCompat.getColor(itemView.context, R.color.white)
            endContainerColor = ContextCompat.getColor(itemView.context, R.color.grey)
            startColorButton = ContextCompat.getColor(itemView.context, R.color.light_pink)
            endColorButton = ContextCompat.getColor(itemView.context, R.color.medium_pink)
        }

        fun onBind(airlineLayout: AirlineLayout) {
            this.item = airlineLayout
            binding.airline = airlineLayout
            binding.bnChoose.apply {
                text = if (airlineLayout.selected) {
                    itemView.resources.getString(R.string.deselect)
                } else {
                    itemView.resources.getString(R.string.choose)
                }
                background.setTint(if (airlineLayout.selected) endColorButton else startColorButton)
            }
            binding.containerAirline.apply {
                setBackgroundColor(if (airlineLayout.selected) endContainerColor else startContainerColor)
            }
        }

        fun onBind(airlineLayout: AirlineLayout, payloads: List<Any>) {
            this.item = airlineLayout
            val isSelected = payloads.last() as Boolean
            if (isSelected) {
                binding.containerAirline.animateColorTransition(startContainerColor, endContainerColor)
                binding.bnChoose.apply {
                    text = itemView.resources.getString(R.string.deselect)
                    animateColorTransition(startColorButton, endColorButton)
                }
            } else {
                binding.containerAirline.animateColorTransition(endContainerColor, startContainerColor)
                binding.bnChoose.apply {
                    text = itemView.resources.getString(R.string.choose)
                    animateColorTransition(endColorButton, startColorButton)
                }
            }
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