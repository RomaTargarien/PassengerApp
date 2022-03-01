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
import com.example.passengerapp.databinding.FragmentPassengerCreatingBinding
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

@FlowPreview
class PassengerCreatingFragment : Fragment() {

    private lateinit var binding: FragmentPassengerCreatingBinding
    val viewModel: PassengerCreatingViewModel by viewModel()
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
        lifecycleScope.launch {
            viewModel.airlinesFlow.collect {
                airlineAdapter.submitList(it)
            }
        }
        binding.tvChooseAirline.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.mainContainer)
            binding.guidelineTop.setGuidelinePercent(0.1f)
            binding.guidelineBottom.setGuidelinePercent(0.65f)
            binding.rvAirlines.visibility = View.VISIBLE
        }
    }

    private fun setUpRecyclerView() {
        airlineAdapter = AirlineAdapter()
        binding.rvAirlines.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding.rvAirlines.adapter = airlineAdapter
    }
}