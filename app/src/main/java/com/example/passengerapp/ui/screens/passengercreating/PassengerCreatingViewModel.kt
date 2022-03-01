package com.example.passengerapp.ui.screens.passengercreating

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passengerapp.model.Airline
import com.example.passengerapp.repository.PassengerRepository
import com.example.passengerapp.ui.base.Validation
import com.example.passengerapp.ui.util.TextInputResource
import com.example.passengerapp.ui.util.toStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@FlowPreview
class PassengerCreatingViewModel(
    private val validation: Validation,
    private val repository: PassengerRepository
) : ViewModel() {

    val name: MutableStateFlow<String> = MutableStateFlow(DEFAULT_NAME)
    val nameValidationResult =
        MutableStateFlow<TextInputResource<String>>(TextInputResource.ErrorInput(null))

    val trips: MutableStateFlow<String> = MutableStateFlow(TRIPS)
    val tripsValidationResult =
        MutableStateFlow<TextInputResource<String>>(TextInputResource.ErrorInput(null))

    private val _airlinesFlow: MutableStateFlow<List<Airline>> = MutableStateFlow(emptyList())
    val airlinesFlow: StateFlow<List<Airline>> = _airlinesFlow

    val createPassengerEnabled = combine(nameValidationResult, tripsValidationResult) { _ ->
        processProgress()
    }.toStateFlow(false, viewModelScope)

    init {
        viewModelScope.launch(Dispatchers.Default) {
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

        viewModelScope.launch {
            val result = repository.getAllAirlines()
            result.data?.let {
                Log.d("TAG", it.size.toString())
                it.filterNot {
                    it.logo == null || it.logo.isEmpty() ||
                            it.country == null || it.country.isEmpty() ||
                            it.established == null || it.established.isEmpty() ||
                            it.headQuaters == null || it.headQuaters.isEmpty() ||
                            it.id == null || it.id == 0.0 ||
                            it.slogan == null || it.slogan.isEmpty() ||
                            it.website == null || it.website.isEmpty()

                }.also {
                   _airlinesFlow.emit(it)
                }
            }
        }
    }

    private fun processProgress(): Boolean {
        return tripsValidationResult.value is TextInputResource.SuccessInput &&
                nameValidationResult.value is TextInputResource.SuccessInput
    }


    companion object {
        private const val DEFAULT_NAME = ""
        private const val TRIPS = ""
    }
}