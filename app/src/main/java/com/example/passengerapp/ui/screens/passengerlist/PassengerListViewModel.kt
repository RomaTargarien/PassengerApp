package com.example.passengerapp.ui.screens.passengerlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.passengerapp.model.Passenger
import com.example.passengerapp.model.request.PassengerRequest
import com.example.passengerapp.repository.PassengerRepository
import com.example.passengerapp.ui.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PassengerListViewModel(private val repository: PassengerRepository) : ViewModel() {

    val refreshSharedFlow: MutableSharedFlow<Unit> = MutableSharedFlow()

    val pagingData: Flow<PagingData<Passenger>> = MutableSharedFlow<Unit>()
        .onStart { emit(Unit) }
        .flatMapLatest { repository.getPassengersResultStream() }
        .cachedIn(viewModelScope)


    fun deletePassengerById(id: String) {
        viewModelScope.launch {
            val result = repository.deletePassenger(id)
            if (result is Resource.Success) {
                refreshSharedFlow.emit(Unit)
            }
        }
    }

    fun undoDelete(passenger: Passenger) {
        viewModelScope.launch {
            val passengerRequest =
                PassengerRequest(passenger.name, passenger.trips, passenger.airline[0].id)
            val result = repository.createPassenger(passengerRequest)
            if (result is Resource.Success) {
                refreshSharedFlow.emit(Unit)
            }
        }
    }
}