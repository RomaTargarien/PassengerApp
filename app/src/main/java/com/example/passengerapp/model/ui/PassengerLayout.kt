package com.example.passengerapp.model.ui

import androidx.recyclerview.widget.RecyclerView
import com.example.passengerapp.R
import com.example.passengerapp.databinding.ItemPassengerBinding
import com.example.passengerapp.model.Airline
import com.example.passengerapp.model.Passenger
import com.example.passengerapp.ui.util.extensions.rotate
import com.example.passengerapp.ui.util.extensions.scale
import com.example.passengerapp.ui.util.extensions.setHeight

data class PassengerLayout(
    val airline: Airline,
    val id: String,
    val name: String,
    val trips: Double,
    var expanded: Boolean = false
)