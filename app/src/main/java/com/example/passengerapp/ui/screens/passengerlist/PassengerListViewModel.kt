package com.example.passengerapp.ui.screens.passengerlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.passengerapp.model.Passenger
import com.example.passengerapp.model.request.PassengerRequest
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
        .flatMapLatest {
            repository.getPassengersResultStream(NETWORK_PAGE_SIZE)
        }
        .cachedIn(viewModelScope)
        .map {
            it.map { passenger ->
                PassengerItemViewModel(passenger)
                    .also(passengerItemViewModels::add)
                    .also(::subscribeToPassengerItemViewExpandEvent)
            }
        }

    val refreshSharedFlow: MutableSharedFlow<Unit> = MutableSharedFlow()

    val undoDeleteFlow: SharedFlow<Passenger>
        get() = _undoDeleteFlow

    private val _undoDeleteFlow: MutableSharedFlow<Passenger> = MutableSharedFlow()
    private val passengerItemViewModels = mutableListOf<PassengerItemViewModel>()

    fun deletePassenger(passenger: Passenger) {
        viewModelScope.launch {
            val result = repository.deletePassenger(passenger.id)
            _undoDeleteFlow.emit(passenger)
            if (result is Resource.Success) {
                clearViewModelsList()
                refreshSharedFlow.emit(Unit)
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            clearViewModelsList()
            refreshSharedFlow.emit(Unit)
        }
    }

    fun undoDelete(passenger: Passenger) {
        viewModelScope.launch {
            val passengerRequest =
                PassengerRequest(
                    airline = passenger.airline[0].id?.toInt() ?: -1,
                    name = passenger.name,
                    trips = passenger.trips
                )
            val result = repository.createPassenger(passengerRequest)
            if (result is Resource.Success) {
                clearViewModelsList()
                refreshSharedFlow.emit(Unit)
            }
        }
    }

    private fun subscribeToPassengerItemViewExpandEvent(passengerItemViewModel: PassengerItemViewModel) {
        viewModelScope.launch {
            passengerItemViewModel.uiEvents.collect { event ->
                passengerItemViewModel.setExpanded(event.isExpanded, event.startAnimation)
                if (event.isExpanded) {
                    passengerItemViewModels
                        .find { it.passengerLayoutFlow.value.expanded && it.passengerLayoutFlow.value.id != event.id }
                        ?.onExpandClickStartAnimation()
                }
            }
        }
    }

    private fun clearViewModelsList() {
        passengerItemViewModels
            .onEach { it.release() }
            .clear()
    }

    override fun onCleared() {
        super.onCleared()
        clearViewModelsList()
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 20
    }
}