package com.example.passengerapp.ui.screens.passengercreating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passengerapp.databinding.BottomSheetAirlineListBinding
import com.example.passengerapp.databinding.FragmentPassengerCreatingBinding
import com.example.passengerapp.ui.util.Resource
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

@FlowPreview
class PassengerCreatingFragment : Fragment() {

    val viewModel: PassengerCreatingViewModel by viewModel()
    private lateinit var binding: FragmentPassengerCreatingBinding
    private lateinit var bottomSheetAirlineBinding: BottomSheetAirlineListBinding
    private lateinit var airlineAdapter: AirlineAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentPassengerCreatingBinding.inflate(inflater, container, false)
            .also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        bottomSheetAirlineBinding = binding.bmSheetAirlinesList
        bottomSheetAirlineBinding.viewModel = viewModel
        bottomSheetAirlineBinding.lifecycleOwner = viewLifecycleOwner
        initializeBottomSheet()
        setUpRecyclerView()
        observeAirlinesLoadingState()
    }

    private fun observeAirlinesLoadingState() {
        viewModel.airlinesState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    result.data?.let {
                        airlineAdapter.submitList(it)
                    }
                }
                is Resource.Error -> {
                }
            }
        }
    }

    private fun initializeBottomSheet() {
        val bottomSheetContainer = bottomSheetAirlineBinding.bottomSheetContainer
        val bottomSheetContainerBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        bottomSheetContainerBehavior.skipCollapsed = true
        bottomSheetContainerBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetContainerBehavior.addBottomSheetCallback(bottomSheetCallback)
        lifecycleScope.launch {
            viewModel.isBottomSheetExpanded.collect { expanded ->
                if (expanded) {
                    bottomSheetContainerBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                } else {
                    bottomSheetContainerBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {}

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            binding.fabShowBottomSheetContainer.animate().rotation((1 - slideOffset) * 180)
                .setDuration(0).start()
            val params = binding.bnCreatePassenger.layoutParams as ViewGroup.MarginLayoutParams
            params.marginEnd = ((1 - (-slideOffset)) * binding.imageView.width).toInt()
            binding.bnCreatePassenger.layoutParams = params
        }
    }

    private fun setUpRecyclerView() {
        airlineAdapter = AirlineAdapter()
        bottomSheetAirlineBinding.rvAirlines.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = airlineAdapter
        }
        airlineAdapter.setOnAirlineSelectedListener {
            viewModel.toggleAirlineLayoutSelection(it)
        }
    }
}