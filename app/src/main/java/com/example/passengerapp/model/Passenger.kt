package com.example.passengerapp.model

import com.google.gson.annotations.SerializedName

data class Passenger(
    @SerializedName("airline") val airline: List<Airline>,
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("trips") val trips: Double,
    @SerializedName("__v") val v: Int,
)