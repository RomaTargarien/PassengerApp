package com.example.passengerapp.ui.screens.passengercreating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passengerapp.databinding.BottomSheetAirlineListBinding
import com.example.passengerapp.databinding.FragmentPassengerCreatingBinding
import com.example.passengerapp.ui.screens.GoBackAppBarBehavior
import com.example.passengerapp.ui.util.Resource
import com.example.passengerapp.ui.util.animateMarginEnd
import com.example.passengerapp.ui.util.rotate
import com.example.passengerapp.ui.util.setUpKeyBoardEventListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


@FlowPreview
class PassengerCreatingFragment : Fragment(),GoBackAppBarBehavior {

    val viewModel: PassengerCreatingViewModel by viewModel()
    private lateinit var binding: FragmentPassengerCreatingBinding
    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {}

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            binding.fabShowBottomSheetContainer.rotate(((1 - slideOffset) * 180))
            binding.bnCreatePassenger.animateMarginEnd((slideOffset * binding.imageView.width))
            binding.tvResultSize.alpha = slideOffset
        }
    }
    private lateinit var bottomSheetAirlineBinding: BottomSheetAirlineListBinding
    private lateinit var airlineAdapter: AirlineAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentPassengerCreatingBinding.inflate(inflater, container, false)
            .also {
                binding = it
                bottomSheetAirlineBinding = binding.bmSheetAirlinesList
            }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        bottomSheetAirlineBinding.viewModel = viewModel
        bottomSheetAirlineBinding.lifecycleOwner = viewLifecycleOwner
        initializeBottomSheet()
        setUpRecyclerView()
        observeAirlinesLoadingState()
        setUpKeyBoardEventListener { isOpen ->
            if (isOpen && !viewModel.isBottomSheetExpanded.value) {
                viewModel.toggleBottomSheetExpandedState()
            }
        }
    }

    private fun observeAirlinesLoadingState() {
        viewModel.airlinesState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    bottomSheetAirlineBinding.progressBar.isVisible = true
                }
                is Resource.Success -> {
                    bottomSheetAirlineBinding.progressBar.isVisible = false
                    result.data?.let {
                        binding.tvResultSize.text = it.size.toString()
                        airlineAdapter.submitList(it)
                    }
                }
                is Resource.Error -> {
                    bottomSheetAirlineBinding.progressBar.isVisible = false
                }
            }
        }
    }

    private fun initializeBottomSheet() {
        val bottomSheetContainer = bottomSheetAirlineBinding.bottomSheetContainer
        val bottomSheetContainerBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        bottomSheetContainerBehavior.addBottomSheetCallback(bottomSheetCallback)
        lifecycleScope.launch {
            viewModel.isBottomSheetExpanded.collect { expanded ->
                if (expanded) {
                    bottomSheetContainerBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                } else {
                    bottomSheetContainerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
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