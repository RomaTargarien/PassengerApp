package com.example.passengerapp.model

import com.google.gson.annotations.SerializedName

data class PassengersPage(
    @SerializedName("data") val data: List<Passenger>,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalPassengers") val totalPassengers: Int
)