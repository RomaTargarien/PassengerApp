package com.example.passengerapp.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.passengerapp.model.Airline
import com.example.passengerapp.model.Passenger
import com.example.passengerapp.model.request.PassengerRequest
import com.example.passengerapp.model.response.PassengerResponse
import com.example.passengerapp.network.PassengerApi
import com.example.passengerapp.ui.util.Constants.NETWORK_PAGE_SIZE
import com.example.passengerapp.ui.util.Resource
import com.example.passengerapp.ui.util.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PassengerRepositoryImpl(private val passengerApi: PassengerApi) : PassengerRepository {

    override fun getPassengersResultStream(): Flow<PagingData<Passenger>> =
        Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PassengerPagingSource(passengerApi) }
        ).flow


    override suspend fun createPassenger(passenger: PassengerRequest): Resource<PassengerResponse> =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = passengerApi.createPassenger(passenger)
                Resource.Success(response)
            }
        }

    override suspend fun deletePassenger(id: String): Resource<PassengerResponse> =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = passengerApi.deletePassenger(id)
                Resource.Success(response)
            }
        }

    override suspend fun getAllAirlines(): Resource<List<Airline>> =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = passengerApi.getAirlines()
                Resource.Success(response)
            }
        }
}