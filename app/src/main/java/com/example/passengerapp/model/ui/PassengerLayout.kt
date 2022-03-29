package com.example.passengerapp.model.ui

import com.example.passengerapp.model.Airline

data class PassengerLayout(
    val airline: Airline,
    val id: String,
    val name: String,
    val trips: Double,
    var selected: Boolean = false
) {
    fun toggleSelection() {
        selected = !selected
    }
}