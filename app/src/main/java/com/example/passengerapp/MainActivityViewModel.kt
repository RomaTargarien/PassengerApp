package com.example.passengerapp

import android.util.Log
import androidx.lifecycle.ViewModel


class MainActivityViewModel(private val repositoryImpl: HelloRepository) : ViewModel(){

    fun sayHello() {
        Log.d("TAG",repositoryImpl.giveHello())
    }
}