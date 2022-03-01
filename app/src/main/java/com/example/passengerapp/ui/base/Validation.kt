package com.example.passengerapp.ui.base

import com.example.passengerapp.ui.util.TextInputResource

interface Validation {
    fun validateName(name: String): TextInputResource<String>
    fun validateTrips(trips: String): TextInputResource<String>
}