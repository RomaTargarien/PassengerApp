package com.example.passengerapp.ui.screens.passengerlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.passengerapp.model.Passenger
import com.example.passengerapp.model.request.PassengerRequest
import com.example.passengerapp.model.ui.PassengerLayout
import com.example.passengerapp.repository.PassengerRepository
import com.example.passengerapp.ui.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PassengerListViewModel(private val repository: PassengerRepository) : ViewModel() {

    private val pagingData: Flow<PagingData<Passenger>> = MutableSharedFlow<Unit>()
        .onStart { emit(Unit) }
        .flatMapLatest { repository.getPassengersResultStream() }
        .cachedIn(viewModelScope)

    val pagingDataLayout = pagingData.map {
        it.map { passenger ->
            PassengerLayout(passenger.airline[0],passenger.id,passenger.name,passenger.trips)
        }
    }

    val refreshSharedFlow: MutableSharedFlow<Unit> = MutableSharedFlow()


    fun deletePassengerById(id: String) {
        viewModelScope.launch {
            val result = repository.deletePassenger(id)
            if (result is Resource.Success) {
                refreshSharedFlow.emit(Unit)
            }
        }
    }

    fun undoDelete(passenger: PassengerLayout) {
        viewModelScope.launch {
            val passengerRequest =
                PassengerRequest(
                    airline = passenger.airline.id?.toInt() ?: -1,
                    name = passenger.name,
                    trips = passenger.trips
                )
            val result = repository.createPassenger(passengerRequest)
            if (result is Resource.Success) {
                refreshSharedFlow.emit(Unit)
            }
        }
    }
}