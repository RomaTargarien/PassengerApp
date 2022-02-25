package com.example.passengerapp.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.passengerapp.model.Passenger
import com.example.passengerapp.network.PassengerApi
import kotlinx.coroutines.flow.Flow

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

    companion object {
        private const val NETWORK_PAGE_SIZE = 20
    }
}