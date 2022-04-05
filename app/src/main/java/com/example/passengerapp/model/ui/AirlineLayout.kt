package com.example.passengerapp.model.ui

data class AirlineLayout(
    val id: Double?,
    val logo: String?,
    val name: String?,
    val slogan: String?,
    var selected: Boolean = false,
    val uniqueID: String,
)