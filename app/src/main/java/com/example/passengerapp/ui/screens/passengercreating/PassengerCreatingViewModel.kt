package com.example.passengerapp.ui.screens.passengercreating

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passengerapp.R
import com.example.passengerapp.model.Airline
import com.example.passengerapp.model.request.PassengerRequest
import com.example.passengerapp.model.ui.AirlineLayout
import com.example.passengerapp.repository.PassengerRepository
import com.example.passengerapp.ui.base.TextInputValidator
import com.example.passengerapp.ui.util.Resource
import com.example.passengerapp.ui.util.TextInputResource
import com.example.passengerapp.ui.util.extensions.toStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

@FlowPreview
class PassengerCreatingViewModel(
    private val textInputValidator: TextInputValidator,
    private val repository: PassengerRepository,
    private val resources: Resources
) : ViewModel() {

    val airlineName: MutableStateFlow<String> = MutableStateFlow(DEFAULT_NAME)

    val airlinesState: LiveData<Resource<List<AirlineLayout>>>
        get() = _airlinesState

    val createPassengerEnabled by lazy {
        combine(nameValidationResult, tripsValidationResult, selectedAirline) { _ ->
            processProgress()
        }.toStateFlow(DEFAULT_PASSENGER_CREATING_ENABLED, viewModelScope)
    }

    val isBottomSheetExpanded: StateFlow<Boolean>
        get() = _isBottomSheetExpanded

    val name: MutableStateFlow<String> = MutableStateFlow(DEFAULT_NAME)
    val nameValidationResult: MutableStateFlow<TextInputResource<String>> =
        MutableStateFlow(TextInputResource.InputInProcess())

    val passengerCreatingState: SharedFlow<Resource<String>>
        get() = _passengerCreatingState
    val snackBarFlow: SharedFlow<String>
        get() = _snackBarFlow

    val trips: MutableStateFlow<String> = MutableStateFlow(DEFAULT_TRIPS)
    val tripsValidationResult: MutableStateFlow<TextInputResource<String>> =
        MutableStateFlow(TextInputResource.InputInProcess())

    private var nameJob: Job? = null
    private val resultPassengerList: MutableStateFlow<List<AirlineLayout>> = MutableStateFlow(listOf())
    private val selectedAirline: MutableStateFlow<AirlineLayout?> = MutableStateFlow(DEFAULT_VALUE)
    private var tripsJob: Job? = null
    private val _airlinesState: MutableLiveData<Resource<List<AirlineLayout>>> = MutableLiveData()
    private val _isBottomSheetExpanded: MutableStateFlow<Boolean> = MutableStateFlow(DEFAULT_EXPAND)
    private val _passengerCreatingState: MutableSharedFlow<Resource<String>> = MutableSharedFlow()
    private val _snackBarFlow: MutableSharedFlow<String> = MutableSharedFlow()


    init {
        downloadAirlines()
        jumpToDefaultValues()
        observeAirlineNameInput()
    }

    fun createPassenger() {
        val airlineId = selectedAirline.value?.id?.toInt() ?: -1
        val passenger = PassengerRequest(
            airline = airlineId,
            name = name.value,
            trips = trips.value.toDouble()
        )
        viewModelScope.launch {
            _passengerCreatingState.emit(Resource.Loading())
            val result = repository.createPassenger(passenger)
            if (result is Resource.Success) {
                _passengerCreatingState.emit(Resource.Success(result.data?.message))
                _snackBarFlow.emit(resources.getString(R.string.passenger_created_successfully))
                jumpToDefaultValues()
            }
            if (result is Resource.Error) {
                _passengerCreatingState.emit(Resource.Error(result.message))
                _snackBarFlow.emit(resources.getString(R.string.passenger_created_error))
            }
        }
    }

    fun toggleAirlineLayoutSelection(airlineLayout: AirlineLayout) {
        var newSelectedAirline: AirlineLayout? = null
        val oldList = resultPassengerList.value
        val newList = mutableListOf<AirlineLayout>().also {
            it.addAll(oldList)
        }
        when {
            (selectedAirline.value == null) -> {
                val newItem = changeItemSelectionInList(airlineLayout, newList, true)
                newSelectedAirline = newItem
            }
            (selectedAirline.value != null && selectedAirline.value == airlineLayout) -> {
                val newItem = changeItemSelectionInList(airlineLayout, newList, false)
                newSelectedAirline = newItem
            }
            (selectedAirline.value != null && selectedAirline.value != airlineLayout) -> {
                val newItem = changeItemSelectionInList(airlineLayout, newList, true)
                changeItemSelectionInList(selectedAirline.value!!, newList, false)
                newSelectedAirline = newItem
            }
        }
        selectedAirline.value = newSelectedAirline
        viewModelScope.launch {
            resultPassengerList.emit(newList)
        }
    }

    fun toggleBottomSheetExpandedState() {
        viewModelScope.launch {
            _isBottomSheetExpanded.emit(!isBottomSheetExpanded.value)
        }
    }

    private fun changeItemSelectionInList(
        item: AirlineLayout,
        newList: MutableList<AirlineLayout>,
        isSelected: Boolean
    ): AirlineLayout? {
        val airlineIndex = newList.indexOf(item)
        val newItem = item.copy(selected = isSelected)
        newList.removeAt(airlineIndex)
        newList.add(airlineIndex, newItem)
        return if (isSelected) newItem else null
    }

    private fun downloadAirlines() {
        viewModelScope.launch {
            _airlinesState.postValue(Resource.Loading())
            val result = repository.getAllAirlines()
            if (result is Resource.Success && result.data != null) {
                filterValidAirlines(result.data).map {
                    AirlineLayout(
                        id = it.id,
                        logo = it.logo,
                        name = it.name,
                        slogan = it.slogan,
                        uniqueID = UUID.randomUUID().toString()
                    )
                }.also {
                    resultPassengerList.emit(it)
                }
            }
            if (result is Resource.Error) {
                _airlinesState.postValue(Resource.Error(result.message))
            }
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
            selectedAirline.value?.let {
                toggleAirlineLayoutSelection(it)
            }
            _isBottomSheetExpanded.emit(false)
        }
        nameJob = viewModelScope.launch {
            name.drop(1)
                .onEach { nameValidationResult.emit(TextInputResource.InputInProcess()) }
                .debounce(DEFAULT_DEBOUNCE_TIME)
                .collect {
                    nameValidationResult.emit(textInputValidator.validateName(it))
                }
        }
        tripsJob = viewModelScope.launch(Dispatchers.Default) {
            trips.drop(1)
                .onEach { tripsValidationResult.emit(TextInputResource.InputInProcess()) }
                .debounce(DEFAULT_DEBOUNCE_TIME)
                .collect {
                    tripsValidationResult.emit(textInputValidator.validateTrips(it))
                }
        }
    }

    private fun filterValidAirlines(airlines: List<Airline>): List<Airline> {
        return airlines.filter { it.isValid() }
    }

    private fun observeAirlineNameInput() {
        viewModelScope.launch {
            combine(resultPassengerList, airlineName) { list, name ->
                Pair(list, name)
            }.collect {
                val list = it.first
                val name = it.second
                list.filter { airlineLayout ->
                    airlineLayout.name!!.startsWith(name)
                }.also {
                    _airlinesState.postValue(Resource.Success(it))
                }
            }
        }
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