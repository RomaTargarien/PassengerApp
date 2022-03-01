package com.example.passengerapp.repository

import androidx.paging.PagingData
import com.example.passengerapp.model.Airline
import com.example.passengerapp.model.Passenger
import com.example.passengerapp.model.request.PassengerRequest
import com.example.passengerapp.model.response.PassengerResponse
import com.example.passengerapp.ui.util.Resource
import kotlinx.coroutines.flow.Flow

interface PassengerRepository {

    fun getPassengersResultStream(): Flow<PagingData<Passenger>>

    suspend fun createPassenger(passenger: PassengerRequest): Resource<PassengerResponse>

    suspend fun deletePassenger(id: String): Resource<PassengerResponse>

    suspend fun getAllAirlines(): Resource<List<Airline>>

}