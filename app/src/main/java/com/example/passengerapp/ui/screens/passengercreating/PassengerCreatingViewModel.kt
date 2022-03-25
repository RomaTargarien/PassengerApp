package com.example.passengerapp.ui.screens.passengercreating

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passengerapp.model.Airline
import com.example.passengerapp.model.request.PassengerRequest
import com.example.passengerapp.model.ui.AirlineLayout
import com.example.passengerapp.repository.PassengerRepository
import com.example.passengerapp.ui.base.TextInputValidator
import com.example.passengerapp.ui.util.Resource
import com.example.passengerapp.ui.util.TextInputResource
import com.example.passengerapp.ui.util.toStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

@FlowPreview
class PassengerCreatingViewModel(
    private val textInputValidator: TextInputValidator,
    private val repository: PassengerRepository
) : ViewModel() {

    private val _resultViewAnimationHasBeenHandled: MutableStateFlow<Boolean> =
        MutableStateFlow(DEFAULT_ANIMATION_VALUE)
    val resultViewAnimationHasBeenHandled: StateFlow<Boolean> =
        _resultViewAnimationHasBeenHandled

    private val _airlinesState: MutableLiveData<Resource<List<AirlineLayout>>> = MutableLiveData()
    val airlinesState: LiveData<Resource<List<AirlineLayout>>> = _airlinesState

    private val _isBottomSheetExpanded: MutableStateFlow<Boolean> =
        MutableStateFlow(DEFAULT_EXPAND)
    val isBottomSheetExpanded: StateFlow<Boolean> = _isBottomSheetExpanded

    private val copiedList: MutableLiveData<List<AirlineLayout>> = MutableLiveData()

    val name: MutableStateFlow<String> = MutableStateFlow(DEFAULT_NAME)
    val nameValidationResult =
        MutableStateFlow<TextInputResource<String>>(TextInputResource.InputInProcess())

    private val _selectedAirline: MutableStateFlow<AirlineLayout?> = MutableStateFlow(DEFAULT_VALUE)
    val selectedAirline: StateFlow<AirlineLayout?> = _selectedAirline

    val trips: MutableStateFlow<String> = MutableStateFlow(DEFAULT_TRIPS)
    val tripsValidationResult =
        MutableStateFlow<TextInputResource<String>>(TextInputResource.InputInProcess())

    val airlineName: MutableStateFlow<String> = MutableStateFlow(DEFAULT_NAME)


    val createPassengerEnabled =
        combine(nameValidationResult, tripsValidationResult, selectedAirline) { _ ->
            processProgress()
        }.toStateFlow(DEFAULT_PASSENGER_CREATING_ENABLED, viewModelScope)

    private val _passengerCreatingState: MutableSharedFlow<Resource<String>> = MutableSharedFlow()
    val passengerCreatingState: SharedFlow<Resource<String>> = _passengerCreatingState

    private val airline: MutableSharedFlow<AirlineLayout> = MutableSharedFlow()

    private var nameJob: Job? = null
    private var tripsJob: Job? = null
    private var airlineNameJob: Job? = null

    init {
        downloadAirlines()
        jumpToDefaultValues()
    }

    fun toggleAirlineLayoutSelection(airlineLayout: AirlineLayout) {
        var newSelectedAirline: AirlineLayout? = null
        airlinesState.value?.data?.let { oldList ->
            val newList = mutableListOf<AirlineLayout>().apply {
                addAll(oldList)
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
            _airlinesState.postValue(Resource.Success(newList))
            _selectedAirline.value = newSelectedAirline
        }
    }

    fun clearSelectedAirline() {
        selectedAirline.value?.let {
            toggleAirlineLayoutSelection(it)
        }
    }

    fun toggleResultViewAnimation() {
        _resultViewAnimationHasBeenHandled.value = true
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
                jumpToDefaultValues()
            }
            if (result is Resource.Error) {
                _passengerCreatingState.emit(Resource.Error(result.message))
            }
        }
    }

    fun downloadAirlines() {
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
                    _airlinesState.postValue(Resource.Success(it))
                    copiedList.postValue(it)
                }
            }
            if (result is Resource.Error) {
                _airlinesState.postValue(Resource.Error(result.message))
            }
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
        airlineNameJob = viewModelScope.launch {
            airlineName
                .debounce(DEFAULT_DEBOUNCE_TIME)
                .drop(1)
                .collect { name ->
                    val list = copiedList.value
                    list.let {
                        it?.filter {
                            it.name?.startsWith(name)!!
                        }.also {
                            _airlinesState.postValue(Resource.Success(it))
                        }
                    }
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
        private const val DEFAULT_ANIMATION_VALUE = false
        private val DEFAULT_VALUE = null
    }
}