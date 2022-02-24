package com.example.passengerapp.ui.screens.passengerlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passengerapp.R
import com.example.passengerapp.databinding.FragmentPassengerListBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PassengerListFragment : Fragment() {

    private lateinit var binding: FragmentPassengerListBinding
    val viewModel: PassengerListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentPassengerListBinding.inflate(inflater, container, false).also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpNavigation()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        val passengersAdapter = PassengerAdapter()
        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.rvPassengers.addItemDecoration(decoration)
        binding.rvPassengers.adapter = passengersAdapter.withLoadStateHeaderAndFooter(
            header = PassengerLoadStateAdapter { passengersAdapter.retry() },
            footer = PassengerLoadStateAdapter { passengersAdapter.retry() }
        )
        binding.rvPassengers.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        lifecycleScope.launch {
            viewModel.pagingData.collectLatest {
                passengersAdapter.submitData(it)
            }
        }
        lifecycleScope.launch {
            passengersAdapter.loadStateFlow.collect { loadState ->
                binding.rvPassengers.isVisible = !(loadState.source.refresh is LoadState.Loading)
                binding.bnRetry.isVisible = loadState.source.refresh is LoadState.Error
                binding.swipeToRefresh.isRefreshing = loadState.source.refresh is LoadState.Loading
            }
        }

        binding.swipeToRefresh.setOnRefreshListener {
            passengersAdapter.refresh()
            binding.swipeToRefresh.isRefreshing = true
        }
    }

    private fun setUpNavigation() {
        binding.ivAddPassenger.setOnClickListener {
            findNavController().navigate(R.id.action_passengerListFragment_to_passengerCreatingFragment)
        }
    }
}