package com.example.passengerapp.ui.screens.passengerlist.list

import com.example.passengerapp.model.Passenger
import com.example.passengerapp.ui.util.uievents.PassengerItemExpandEvent
import com.example.passengerapp.model.ui.PassengerLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PassengerItemViewModel(val passenger: Passenger) {

    var layoutIsVisible: Boolean = DEFAULT_LAYOUT_VISIBILITY

    val passengerLayoutFlow: MutableStateFlow<PassengerLayout> =
        MutableStateFlow(createLayout(DEFAULT_EXPANDED, DEFAULT_ANIMATION_LAUNCH))

    val uiEvents: MutableSharedFlow<PassengerItemExpandEvent> = MutableSharedFlow()

    private var isExpanded: Boolean = DEFAULT_EXPANDED

    private var passengerViewModelScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    fun onExpandedAnimationEnded() {
        passengerViewModelScope.launch {
            uiEvents.emit(PassengerItemExpandEvent(passenger.id, isExpanded, false))
        }
    }

    fun onExpandClickStartAnimation() {
        passengerViewModelScope.launch {
            isExpanded = !isExpanded
            if (layoutIsVisible) {
                uiEvents.emit(PassengerItemExpandEvent(passenger.id, isExpanded, true))
            } else {
                uiEvents.emit(PassengerItemExpandEvent(passenger.id, isExpanded, false))
            }
        }
    }

    fun release() {
        passengerViewModelScope.cancel()
    }

    fun setExpanded(expanded: Boolean, startExpandAnimation: Boolean) {
        passengerViewModelScope.launch {
            isExpanded = expanded
            passengerLayoutFlow.emit(createLayout(isExpanded, startExpandAnimation))
        }
    }


    private fun createLayout(expanded: Boolean, startExpandAnimation: Boolean): PassengerLayout =
        PassengerLayout(
            airline = passenger.airline[0],
            expanded = expanded,
            id = passenger.id,
            name = passenger.name,
            startExpandAnimation = startExpandAnimation,
            trips = passenger.trips
        )

    companion object {
        private const val DEFAULT_ANIMATION_LAUNCH = false
        private const val DEFAULT_EXPANDED = false
        private const val DEFAULT_LAYOUT_VISIBILITY = false
    }
}