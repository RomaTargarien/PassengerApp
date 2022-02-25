package com.example.passengerapp.ui.screens.passengerlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.passengerapp.model.Passenger
import com.example.passengerapp.repository.PassengerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart

class PassengerListViewModel(private val repository: PassengerRepository) : ViewModel() {

    val pagingData: Flow<PagingData<Passenger>> = MutableSharedFlow<Unit>()
        .onStart { emit(Unit) }
        .flatMapLatest { repository.getPassengersResultStream() }
        .cachedIn(viewModelScope)

}