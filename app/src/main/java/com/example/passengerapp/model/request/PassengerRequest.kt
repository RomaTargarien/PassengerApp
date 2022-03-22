package com.example.passengerapp.model.request

data class PassengerRequest(
    val airline: Int,
    val name: String,
    val trips: Double
)