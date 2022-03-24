package com.example.passengerapp.ui.screens.passengercreating

import com.example.passengerapp.model.ui.AirlineLayout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AirlineListSelectionHelper {

    private val _selectedAirline: MutableStateFlow<AirlineLayout?> = MutableStateFlow(null)
    val selectedAirline: StateFlow<AirlineLayout?> = _selectedAirline

    private val _newListFlow: MutableStateFlow<List<AirlineLayout>?> = MutableStateFlow(null)
    val newListFlow: StateFlow<List<AirlineLayout>?> = _newListFlow

    fun toggleAirlineLayoutSelection(airlineLayout: AirlineLayout, oldList: List<AirlineLayout>) {
        val state = createListSelectionState(airlineLayout)
        val newList = mutableListOf<AirlineLayout>().apply { addAll(oldList) }
        when (state) {
            is ListSelectionBehavior.Select -> {
                val itemSelected = state.selectedAirlineLayout!!
                changeAirlineLayoutItemSelectionInList(newList, oldList, itemSelected, true).also {
                    _selectedAirline.value = it
                }
            }
            is ListSelectionBehavior.Unselect -> {
                val itemUnselected = state.unSelectedAirlineLayout!!
                changeAirlineLayoutItemSelectionInList(newList, oldList, itemUnselected, false).also {
                    _selectedAirline.value = it
                }
            }
            is ListSelectionBehavior.SelectAndUnSelect -> {
                val itemSelected = state.unSelectedAirlineLayout!!
                changeAirlineLayoutItemSelectionInList(newList, oldList, itemSelected, false)
                val itemUnselected = state.selectedAirlineLayout!!
                changeAirlineLayoutItemSelectionInList(newList, oldList, itemUnselected, true).also {
                    _selectedAirline.value = it
                }
            }
        }
        _newListFlow.value = newList
    }

    private fun changeAirlineLayoutItemSelectionInList(
        newList: MutableList<AirlineLayout>,
        oldList: List<AirlineLayout>,
        airlineLayout: AirlineLayout,
        selected: Boolean
    ): AirlineLayout? {
        val airlineIndex = oldList.indexOf(airlineLayout)
        val newItem = airlineLayout.copy(selected = selected)
        newList.removeAt(airlineIndex)
        newList.add(airlineIndex, newItem)
        return if (selected) newItem else null
    }


    private fun createListSelectionState(
        airlineLayout: AirlineLayout
    ): ListSelectionBehavior? =
        when {
            selectedAirline.value == null -> {
                ListSelectionBehavior.Select(airlineLayout)
            }
            airlineLayout == selectedAirline.value -> {
                ListSelectionBehavior.Unselect(airlineLayout)
            }
            selectedAirline.value != null && airlineLayout != selectedAirline.value -> {
                ListSelectionBehavior.SelectAndUnSelect(
                    selectedAirlineLayout = airlineLayout,
                    unselectedAirlineLayout = selectedAirline.value
                )
            }
            else -> null
        }

    sealed class ListSelectionBehavior(
        val selectedAirlineLayout: AirlineLayout? = null,
        val unSelectedAirlineLayout: AirlineLayout? = null
    ) {
        class Select(selectedAirlineLayout: AirlineLayout) :
            ListSelectionBehavior(selectedAirlineLayout = selectedAirlineLayout)

        class Unselect(unSelectedAirlineLayout: AirlineLayout) :
            ListSelectionBehavior(unSelectedAirlineLayout = unSelectedAirlineLayout)

        class SelectAndUnSelect(
            selectedAirlineLayout: AirlineLayout,
            unselectedAirlineLayout: AirlineLayout?
        ) : ListSelectionBehavior(
            selectedAirlineLayout = selectedAirlineLayout,
            unSelectedAirlineLayout = unselectedAirlineLayout
        )
    }
}