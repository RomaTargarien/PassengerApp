package com.example.passengerapp.repository

import androidx.paging.PagingData
import com.example.passengerapp.model.Passenger
import kotlinx.coroutines.flow.Flow

interface PassengerRepository {

    fun getPassengersResultStream(): Flow<PagingData<Passenger>>

}