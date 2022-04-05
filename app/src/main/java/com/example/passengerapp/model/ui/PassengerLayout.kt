package com.example.passengerapp.model.ui

import com.example.passengerapp.model.Airline

data class PassengerLayout(
    val airline: Airline,
    val expanded: Boolean,
    val id: String,
    val name: String,
    val startExpandAnimation: Boolean,
    val trips: Double
)