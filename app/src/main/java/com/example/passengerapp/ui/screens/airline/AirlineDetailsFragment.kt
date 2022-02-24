package com.example.passengerapp.ui.screens.airline

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.passengerapp.R
import com.example.passengerapp.databinding.FragmentAirlineDetailsBinding


class AirlineDetailsFragment : Fragment() {

    private lateinit var binding: FragmentAirlineDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentAirlineDetailsBinding.inflate(inflater,container,false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}