package com.example.passengerapp.ui.screens.passengercreating

import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passengerapp.R
import com.example.passengerapp.databinding.FragmentPassengerCreatingBinding
import com.example.passengerapp.model.Airline
import com.example.passengerapp.ui.util.Event
import com.example.passengerapp.ui.util.Resource
import com.example.passengerapp.ui.util.snackbar
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

@FlowPreview
class PassengerCreatingFragment : Fragment() {

    val viewModel: PassengerCreatingViewModel by viewModel()
    private lateinit var binding: FragmentPassengerCreatingBinding
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
        setUpRecyclerView()
        observeAirlinesLoadingState()
        observeAirlinesListExpandedState()
        observeSelectedAirline()
        observePassengerCreating()
    }

    private fun observeAirlinesLoadingState() {
        viewModel.airlinesState.observe(viewLifecycleOwner) { event ->
            when (val result = event.data()) {
                is Resource.Loading -> {
                    binding.pbAirlinesState.visibility = View.VISIBLE
                    binding.ivAirlineState.visibility = View.GONE
                    binding.tvChooseAirline.alpha = 0.5f
                    binding.tvChooseAirline.isEnabled = false
                }
                is Resource.Success -> {
                    handleAirlineDownloadedResult(event)
                    result.data?.let { airlineAdapter.submitList(it) }
                }
                is Resource.Error -> {
                    handleAirlineDownloadedResult(event)
                }
            }
        }
    }

    private fun observeAirlinesListExpandedState() {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.isAirlinesListExpanded.collect { expanded ->
                TransitionManager.beginDelayedTransition(binding.mainContainer)
                binding.guidelineTop.setGuidelinePercent(
                    if (expanded) GUIDELINE_TOP_EXPANDED_STATE else GUIDELINE_TOP_INITIAL_STATE
                )
                binding.guidelineBottom.setGuidelinePercent(
                    if (expanded) GUIDELINE_BOTTOM_EXPANDED_STATE else GUIDELINE_BOTTOM_INITIAL_STATE
                )
                binding.rvAirlines.isVisible = expanded
                binding.tvHi.isVisible = !expanded
            }
        }
    }

    private fun observeSelectedAirline() {
        lifecycleScope.launch {
            viewModel.selectedAirline.collectLatest { airline ->
                TransitionManager.beginDelayedTransition(binding.containerPassengerCreating)
                binding.containerSelectedAirline.isVisible = airline != null
                airline?.let {
                    Picasso.get().load(airline.logo).into(binding.ivAirlineLogo)
                    binding.tvAirlineTitle.text = airline.name
                }
            }
        }
    }

    private fun observePassengerCreating() {
        lifecycleScope.launch {
            viewModel.passengerCreatingState.collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        TransitionManager.beginDelayedTransition(binding.containerPassengerCreating)
                        binding.bnCreatePassenger.text = ""
                        binding.pbPassengerCreating.isVisible = true
                    }
                    is Resource.Success -> {
                        TransitionManager.beginDelayedTransition(binding.containerPassengerCreating)
                        binding.bnCreatePassenger.text = resources.getString(R.string.create)
                        binding.pbPassengerCreating.isVisible = false
                        snackbar(resources.getString(R.string.passenger_created_successfully))
                    }
                    is Resource.Error -> {
                        snackbar(resources.getString(R.string.passenger_created_error))
                    }
                }
            }
        }
    }

    private fun handleAirlineDownloadedResult(event: Event<Resource<List<Airline>>>) {
        val resultSuccess = event.data() is Resource.Success
        binding.ivAirlineState.apply {
            setImageResource(if (resultSuccess) R.drawable.ic_icon_success else R.drawable.ic_baseline_replay_24)
            isVisible = !event.hasBeenHandled
            setOnClickListener {
                if (resultSuccess) return@setOnClickListener
                else viewModel.downloadAirlines()
            }
        }
        binding.tvChooseAirline.apply {
            alpha = if (resultSuccess) 1f else 0.5f
            isEnabled = resultSuccess
        }
        binding.pbAirlinesState.visibility = View.GONE
        if (resultSuccess && !event.hasBeenHandled) {
            toggleSuccessImageVisibility()
            event.hasBeenHandled()
        }
    }

    private fun setUpRecyclerView() {
        airlineAdapter = AirlineAdapter(viewModel.adapterSelectedAirlinePosition.value)
        airlineAdapter.setOnChosenAirlineListener { position ->
            viewModel.changeSelectedAirline(position)
        }
        binding.rvAirlines.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = airlineAdapter
        }
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.airlineRemovingFlow.collect {
                airlineAdapter.airlineRemovingListener?.let { click ->
                    viewModel.adapterSelectedAirlinePosition.value?.let(click)
                }
            }
        }
    }

    private fun toggleSuccessImageVisibility() {
        lifecycleScope.launch {
            delay(2000)
            TransitionManager.beginDelayedTransition(binding.mainContainer)
            binding.ivAirlineState.visibility = View.GONE
        }
    }

    companion object {
        private const val GUIDELINE_TOP_INITIAL_STATE = 0.3f
        private const val GUIDELINE_BOTTOM_INITIAL_STATE = 0.85f
        private const val GUIDELINE_TOP_EXPANDED_STATE = 0.15f
        private const val GUIDELINE_BOTTOM_EXPANDED_STATE = 0.7f
    }
}