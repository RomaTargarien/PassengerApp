package com.example.passengerapp.network

import com.example.passengerapp.model.PassengersPage
import com.example.passengerapp.model.request.PassengerRequest
import com.example.passengerapp.model.response.PassengerResponse
import retrofit2.Response
import retrofit2.http.*

interface PassengerApi {

    @GET("passenger?")
    suspend fun getPageEndPoint(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): PassengersPage

    @DELETE("passenger/{id}")
    suspend fun deletePassenger(@Path("id") passengerId: String): Response<PassengerResponse>

    @POST("passenger")
    suspend fun createPassenger(@Body requestBody: PassengerRequest): Response<PassengerResponse>
}