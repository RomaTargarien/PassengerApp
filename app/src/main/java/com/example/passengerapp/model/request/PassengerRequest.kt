package com.example.passengerapp.model.request

data class PassengerRequest(
    val name: String,
    val trips: Double,
    val airline: Int
)