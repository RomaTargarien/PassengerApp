package com.example.passengerapp.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.passengerapp.model.Passenger
import com.example.passengerapp.model.request.PassengerRequest
import com.example.passengerapp.network.PassengerApi
import com.example.passengerapp.ui.util.Resource
import com.example.passengerapp.ui.util.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PassengerRepositoryImpl(private val passengerApi: PassengerApi) : PassengerRepository {

    override fun getPassengersResultStream(): Flow<PagingData<Passenger>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PassengerPagingSource(passengerApi) }
        ).flow
    }

    override suspend fun deletePassenger(id: String) = withContext(Dispatchers.IO) {
        safeCall {
            val response = passengerApi.deletePassenger(id)
            if (response.isSuccessful) {
                Resource.Success(response.body())
            } else {
                Resource.Error(response.message())
            }
        }
    }

    override suspend fun createPassenger(passenger: PassengerRequest) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = passengerApi.createPassenger(passenger)
                if (response.isSuccessful) {
                    Resource.Success(response.body())
                } else {
                    Resource.Error(response.message())
                }
            }
        }

    companion object {
        private const val NETWORK_PAGE_SIZE = 20
    }
}