package com.example.passengerapp.ui.screens.passengerlist.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.passengerapp.databinding.ItemLoadStateFooterBinding

class PassengerLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<PassengerLoadStateAdapter.PassengerLoadStateViewHolder>() {

    inner class PassengerLoadStateViewHolder(val binding: ItemLoadStateFooterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState,retry: () -> Unit) {
            binding.retryButton.setOnClickListener {
                retry.invoke()
            }
            if (loadState is LoadState.Error) {
                binding.errorMsg.text = loadState.error.localizedMessage
            }
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.retryButton.isVisible = loadState is LoadState.Error
            binding.errorMsg.isVisible = loadState is LoadState.Error
        }
    }

    override fun onBindViewHolder(holder: PassengerLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState, retry)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): PassengerLoadStateViewHolder {
        val binding = ItemLoadStateFooterBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PassengerLoadStateViewHolder(binding)
    }
}