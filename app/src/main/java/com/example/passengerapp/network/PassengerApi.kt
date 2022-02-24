package com.example.passengerapp.network

import com.example.passengerapp.model.PageEndPoint
import retrofit2.http.GET
import retrofit2.http.Query

interface PassengerApi {

    @GET("passenger?")
    suspend fun getPageEndPoint(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): PageEndPoint
}