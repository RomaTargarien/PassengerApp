package com.example.passengerapp.ui.screens.passengercreating


import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.passengerapp.BR
import com.example.passengerapp.R
import com.example.passengerapp.databinding.ItemAirlineBinding
import com.example.passengerapp.model.ui.AirlineLayout
import com.example.passengerapp.ui.util.extensions.animateColorTransition

@RequiresApi(Build.VERSION_CODES.M)
class AirlineAdapter : ListAdapter<AirlineLayout, RecyclerView.ViewHolder>(AirlineDiffCalBack()) {

    private var onAirlineSelectedListener: ((AirlineLayout) -> Unit)? = null

    fun setOnAirlineSelectedListener(listener: ((AirlineLayout) -> Unit)) {
        onAirlineSelectedListener = listener
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
            startContainerColor = itemView.resources.getColor(R.color.white, itemView.context.theme)
            endContainerColor = itemView.resources.getColor(R.color.grey, itemView.context.theme)
            startColorButton =
                itemView.resources.getColor(R.color.light_pink, itemView.context.theme)
            endColorButton =
                itemView.resources.getColor(R.color.medium_pink, itemView.context.theme)
        }

        fun onBind(airlineLayout: AirlineLayout) {
            this.item = airlineLayout
            binding.setVariable(BR.airline, airlineLayout)
            binding.bnChoose.text = if (airlineLayout.selected) "Deselect" else "Choose"
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemAirlineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AirlineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as AirlineViewHolder).onBind(currentList[position])
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            (holder as AirlineViewHolder).onBind(currentList[position], payloads)
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