package com.example.passengerapp.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.passengerapp.model.Passenger
import com.example.passengerapp.network.PassengerApi
import retrofit2.HttpException
import java.io.IOException

class PassengerPagingSource(
    private val passengerApi: PassengerApi
): PagingSource<Int,Passenger>() {

    override fun getRefreshKey(state: PagingState<Int, Passenger>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Passenger> {
        val position = params.key ?: STARTING_PAGE_INDEX
        return try {
            val response = passengerApi.getPageEndPoint(position, NETWORK_PAGE_SIZE)
            val passengers = response.data
            val nextKey = if (passengers.isEmpty()) {
                null
            } else {
                position + 1
            }
            LoadResult.Page(
                data = passengers,
                prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 0
        private const val NETWORK_PAGE_SIZE = 20
    }
}