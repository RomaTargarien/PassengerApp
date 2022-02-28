package com.example.passengerapp.ui.util

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?) : Resource<T>(data)
    class Error<T>(messsage: String) : Resource<T>(message = messsage)
    class Loading<T> : Resource<T>()
}