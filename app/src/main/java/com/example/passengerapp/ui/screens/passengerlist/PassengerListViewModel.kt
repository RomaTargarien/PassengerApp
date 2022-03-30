package com.example.passengerapp.ui.screens.passengerlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.passengerapp.model.request.PassengerRequest
import com.example.passengerapp.model.ui.PassengerLayout
import com.example.passengerapp.repository.PassengerRepository
import com.example.passengerapp.ui.screens.passengerlist.list.PassengerItemViewModel
import com.example.passengerapp.ui.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class PassengerListViewModel(private val repository: PassengerRepository) : ViewModel() {

    val pagingData: Flow<PagingData<PassengerItemViewModel>> = MutableSharedFlow<Unit>()
        .onStart { emit(Unit) }
        .flatMapLatest { repository.getPassengersResultStream() }
        .cachedIn(viewModelScope)
        .map { it.map { passenger -> PassengerItemViewModel(passenger) } }

    val refreshSharedFlow: MutableSharedFlow<Unit> = MutableSharedFlow()

    fun deletePassengerById(id: String) {
        viewModelScope.launch {
            val result = repository.deletePassenger(id)
            if (result is Resource.Success) {
                refreshSharedFlow.emit(Unit)
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            refreshSharedFlow.emit(Unit)
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