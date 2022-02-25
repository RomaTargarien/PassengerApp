package com.example.passengerapp.model

data class PageEndPoint(
    val data: List<Passenger>,
    val totalPages: Int,
    val totalPassengers: Int
)