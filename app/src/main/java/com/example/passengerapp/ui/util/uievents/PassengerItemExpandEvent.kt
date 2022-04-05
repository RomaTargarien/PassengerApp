package com.example.passengerapp.ui.util.uievents

data class PassengerItemExpandEvent(
    val id: String,
    val isExpanded: Boolean,
    val startAnimation: Boolean
)
