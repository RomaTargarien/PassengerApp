package com.example.passengerapp.ui.screens.passengercreating

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passengerapp.model.Airline
import com.example.passengerapp.model.request.PassengerRequest
import com.example.passengerapp.repository.PassengerRepository
import com.example.passengerapp.ui.base.Validation
import com.example.passengerapp.ui.util.Event
import com.example.passengerapp.ui.util.Resource
import com.example.passengerapp.ui.util.TextInputResource
import com.example.passengerapp.ui.util.toStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@FlowPreview
class PassengerCreatingViewModel(
    private val validation: Validation,
    private val repository: PassengerRepository
) : ViewModel() {

    private val _airlineRemovingFlow: MutableSharedFlow<Unit> = MutableSharedFlow()
    val airlineRemovingFlow: SharedFlow<Unit> = _airlineRemovingFlow

    private val _airlinesState: MutableLiveData<Event<Resource<List<Airline>>>> = MutableLiveData()
    val airlinesState: LiveData<Event<Resource<List<Airline>>>> = _airlinesState

    private val _adapterSelectedAirlinePosition: MutableStateFlow<Int?> =
        MutableStateFlow(DEFAULT_VALUE)
    val adapterSelectedAirlinePosition: StateFlow<Int?> = _adapterSelectedAirlinePosition

    private val _isAirlinesListExpanded: MutableStateFlow<Boolean> =
        MutableStateFlow(DEFAULT_EXPAND)
    val isAirlinesListExpanded: StateFlow<Boolean> = _isAirlinesListExpanded

    val name: MutableStateFlow<String> = MutableStateFlow(DEFAULT_NAME)
    val nameValidationResult =
        MutableStateFlow<TextInputResource<String>>(TextInputResource.InputInProcess())

    private val _selectedAirline: MutableStateFlow<Airline?> = MutableStateFlow(DEFAULT_VALUE)
    val selectedAirline: StateFlow<Airline?> = _selectedAirline

    val trips: MutableStateFlow<String> = MutableStateFlow(DEFAULT_TRIPS)
    val tripsValidationResult =
        MutableStateFlow<TextInputResource<String>>(TextInputResource.InputInProcess())

    val createPassengerEnabled =
        combine(nameValidationResult, tripsValidationResult, selectedAirline) { _ ->
            processProgress()
        }.toStateFlow(DEFAULT_PASSENGER_CREATING_ENABLED, viewModelScope)

    private val _passengerCreatingState: MutableSharedFlow<Resource<String>> = MutableSharedFlow()
    val passengerCreatingState: SharedFlow<Resource<String>> = _passengerCreatingState

    private var nameJob: Job? = null
    private var tripsJob: Job? = null

    init {
        downloadAirlines()
        jumpToDefaultValues()
    }

    fun changeSelectedAirline(position: Int?) {
        var selectedAirline: Airline? = null
        if (position != null) {
            selectedAirline = airlinesState.value?.data()?.data?.get(position)
        }
        viewModelScope.launch {
            _adapterSelectedAirlinePosition.value = position
            _selectedAirline.emit(selectedAirline)
        }
    }

    fun createPassenger() {
        val airlineId = selectedAirline.value?.id!!.toInt()
        val passenger = PassengerRequest(name.value, trips.value.toDouble(), airlineId)
        viewModelScope.launch {
            _passengerCreatingState.emit(Resource.Loading())
            val result = repository.createPassenger(passenger)
            if (result is Resource.Success) {
                _passengerCreatingState.emit(Resource.Success(result.data?.message))
                jumpToDefaultValues()
            }
            if (result is Resource.Error) {
                _passengerCreatingState.emit(Resource.Error(result.message))
            }
        }
    }

    fun downloadAirlines() {
        viewModelScope.launch {
            _airlinesState.postValue(Event(Resource.Loading()))
            val result = repository.getAllAirlines()
            if (result is Resource.Success && result.data != null) {
                _airlinesState.postValue(Event(Resource.Success(filterValidAirlines(result.data))))
            }
            if (result is Resource.Error) {
                _airlinesState.postValue(Event(Resource.Error(result.message)))
            }
        }
    }

    fun removeAirline() {
        viewModelScope.launch {
            _airlineRemovingFlow.emit(Unit)
        }
    }

    fun toggleExpandedAirlinesListState() {
        viewModelScope.launch {
            _isAirlinesListExpanded.emit(!isAirlinesListExpanded.value)
        }
    }

    private fun jumpToDefaultValues() {
        nameJob?.cancel()
        tripsJob?.cancel()
        viewModelScope.launch {
            name.emit(DEFAULT_NAME)
            trips.emit(DEFAULT_TRIPS)
            tripsValidationResult.emit(TextInputResource.InputInProcess())
            nameValidationResult.emit(TextInputResource.InputInProcess())
            removeAirline()
        }
        nameJob = viewModelScope.launch {
            name.drop(1).onEach {
                nameValidationResult.emit(TextInputResource.InputInProcess())
            }.debounce(DEFAULT_DEBOUNCE_TIME).collect {
                nameValidationResult.emit(validation.validateName(it))
            }
        }
        tripsJob = viewModelScope.launch(Dispatchers.Default) {
            trips.drop(1).onEach {
                tripsValidationResult.emit(TextInputResource.InputInProcess())
            }.debounce(DEFAULT_DEBOUNCE_TIME).collect {
                tripsValidationResult.emit(validation.validateTrips(it))
            }
        }
    }

    private fun filterValidAirlines(airlines: List<Airline>): List<Airline> {
        return airlines.filter { it.isValid() }
    }

    private fun processProgress(): Boolean {
        return tripsValidationResult.value is TextInputResource.SuccessInput &&
                nameValidationResult.value is TextInputResource.SuccessInput &&
                selectedAirline.value != null
    }

    companion object {
        private const val DEFAULT_DEBOUNCE_TIME = 300L
        private const val DEFAULT_NAME = ""
        private const val DEFAULT_TRIPS = ""
        private const val DEFAULT_EXPAND = false
        private const val DEFAULT_PASSENGER_CREATING_ENABLED = false
        private val DEFAULT_VALUE = null
    }
}