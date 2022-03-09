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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@FlowPreview
class PassengerCreatingViewModel(
    private val validation: Validation,
    private val repository: PassengerRepository
) : ViewModel() {

    private val _airlinesState: MutableLiveData<Event<Resource<List<Airline>>>> = MutableLiveData()
    val airlinesState: LiveData<Event<Resource<List<Airline>>>> = _airlinesState

    private val _adapterSelectedAirlinePosition: MutableStateFlow<Int?> =
        MutableStateFlow(DEFAULT_VALUE)
    val adapterSelectedAirlinePosition: StateFlow<Int?> = _adapterSelectedAirlinePosition

    val name: MutableStateFlow<String> = MutableStateFlow(DEFAULT_NAME)
    val nameValidationResult =
        MutableStateFlow<TextInputResource<String>>(TextInputResource.ErrorInput(DEFAULT_VALUE))

    val trips: MutableStateFlow<String> = MutableStateFlow(DEFAULT_TRIPS)
    val tripsValidationResult =
        MutableStateFlow<TextInputResource<String>>(TextInputResource.ErrorInput(DEFAULT_VALUE))

    private val _selectedAirline: MutableStateFlow<Airline?> = MutableStateFlow(DEFAULT_VALUE)
    val selectedAirline: StateFlow<Airline?> = _selectedAirline

    val createPassengerEnabled =
        combine(nameValidationResult, tripsValidationResult, selectedAirline) { _ ->
            processProgress()
        }.toStateFlow(DEFAULT_PASSENGER_CREATING_ENABLED, viewModelScope)

    private val _isAirlinesListExpanded: MutableStateFlow<Boolean> =
        MutableStateFlow(DEFAULT_EXPAND)
    val isAirlinesListExpanded: StateFlow<Boolean> = _isAirlinesListExpanded

    private val _passengerCreatingState: MutableLiveData<Resource<String>> = MutableLiveData()
    val passengerCreatingState: LiveData<Resource<String>> = _passengerCreatingState

    private var nameJob: Job? = null

    init {
        downloadAirlines()

        nameJob = viewModelScope.launch(Dispatchers.Default) {
            name.drop(1).onEach {
                nameValidationResult.emit(TextInputResource.InputInProcess())
            }.debounce(300).collect {
                nameValidationResult.emit(validation.validateName(it))
            }
        }

        viewModelScope.launch(Dispatchers.Default) {
            trips.drop(1).onEach {
                tripsValidationResult.emit(TextInputResource.InputInProcess())
            }.debounce(300).collect {
                tripsValidationResult.emit(validation.validateTrips(it))
            }
        }
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

    fun createPassenger() {
        val airlineId = selectedAirline.value?.id!!.toInt()
        val passenger = PassengerRequest(name.value,trips.value.toDouble(),airlineId)
        viewModelScope.launch {
            _passengerCreatingState.postValue(Resource.Loading())
            delay(2000)
            val result = repository.createPassenger(passenger)
            if (result is Resource.Success) {
                _passengerCreatingState.postValue(Resource.Success(result.data?.message))
            }
            if (result is Resource.Error) {
                _passengerCreatingState.postValue(Resource.Error(result.message))
            }
        }
    }

    fun toggleExpandedAirlinesListState() {
        viewModelScope.launch {
            _isAirlinesListExpanded.emit(!isAirlinesListExpanded.value)
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
        private const val DEFAULT_NAME = ""
        private const val DEFAULT_TRIPS = ""
        private const val DEFAULT_EXPAND = false
        private const val DEFAULT_PASSENGER_CREATING_ENABLED = false
        private val DEFAULT_VALUE = null
    }
}