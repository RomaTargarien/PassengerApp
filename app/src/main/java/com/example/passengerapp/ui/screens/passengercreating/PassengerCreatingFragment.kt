package com.example.passengerapp.ui.screens.passengercreating

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passengerapp.databinding.BottomSheetAirlineListBinding
import com.example.passengerapp.databinding.FragmentPassengerCreatingBinding
import com.example.passengerapp.ui.screens.contract.GoBackAppBarBehavior
import com.example.passengerapp.ui.util.Resource
import com.example.passengerapp.ui.util.extensions.animateMarginEnd
import com.example.passengerapp.ui.util.extensions.rotate
import com.example.passengerapp.ui.util.extensions.setUpKeyBoardEventListener
import com.example.passengerapp.ui.util.extensions.snackbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


@FlowPreview
@RequiresApi(Build.VERSION_CODES.M)
class PassengerCreatingFragment : Fragment(), GoBackAppBarBehavior {

    val viewModel: PassengerCreatingViewModel by viewModel()

    private val binding: FragmentPassengerCreatingBinding
        get() = _binding!!
    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {}

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            binding.fabShowBottomSheetContainer.rotate(((slideOffset) * 180))
            binding.bnCreatePassenger.animateMarginEnd((slideOffset * binding.imageView.width))
            binding.tvResultSize.alpha = slideOffset
        }
    }

    private var _binding: FragmentPassengerCreatingBinding? = null

    private lateinit var airlineAdapter: AirlineAdapter
    private lateinit var bottomSheetAirlineBinding: BottomSheetAirlineListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentPassengerCreatingBinding.inflate(inflater, container, false)
            .also {
                _binding = it
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
        observePassengerCreatingState()
        observePassengerCreatingStateSnackbar()
        setUpKeyBoardEventListener { isOpen ->
            if (isOpen && !viewModel.isBottomSheetExpanded.value) {
                viewModel.toggleBottomSheetExpandedState()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private fun observePassengerCreatingState() {
        lifecycleScope.launch {
            viewModel.passengerCreatingState.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.pbCreatePassenger.isVisible = true
                    }
                    is Resource.Success -> {
                        binding.pbCreatePassenger.isVisible = false
                    }
                    is Resource.Error -> {
                        binding.pbCreatePassenger.isVisible = false
                    }
                }
            }
        }
    }

    private fun observePassengerCreatingStateSnackbar() {
        lifecycleScope.launch {
            viewModel.snackBarFlow.collect { message ->
                snackbar(message)
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