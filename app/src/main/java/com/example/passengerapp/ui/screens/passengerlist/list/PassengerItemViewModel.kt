package com.example.passengerapp.ui.screens.passengerlist.list

import com.example.passengerapp.model.Passenger
import com.example.passengerapp.model.ui.PassengerLayout

class PassengerItemViewModel(val passenger: Passenger) {

    private var _passengerLayout: PassengerLayout? = null
    val passengerLayout: PassengerLayout
        get() = _passengerLayout!!

    fun getLayout(): PassengerLayout {
        return if (_passengerLayout == null) {
            _passengerLayout = PassengerLayout(
                airline = passenger.airline[0],
                id = passenger.id,
                name = passenger.name,
                trips = passenger.trips
            )
            passengerLayout
        } else {
            passengerLayout
        }
    }

    fun changeExpandedState(expanded: Boolean) {
        _passengerLayout?.expanded = expanded
    }
}