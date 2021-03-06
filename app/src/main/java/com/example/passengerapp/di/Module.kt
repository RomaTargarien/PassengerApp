package com.example.passengerapp

import com.example.passengerapp.network.PassengerApi
import com.example.passengerapp.repository.PassengerRepository
import com.example.passengerapp.repository.PassengerRepositoryImpl
import com.example.passengerapp.ui.screens.MainActivityViewModel
import com.example.passengerapp.ui.screens.passengerlist.PassengerListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@JvmField
val appModule = module {

    factory { provideRetrofitInstance() }

    factory { provideNetworkApi(get()) }

    single<PassengerRepository> { PassengerRepositoryImpl(get()) }

    viewModel { MainActivityViewModel() }

    viewModel { PassengerListViewModel(get()) }
}

fun provideRetrofitInstance(): Retrofit =
    Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

fun provideNetworkApi(retrofit: Retrofit): PassengerApi =
    retrofit.create(PassengerApi::class.java)

private const val BASE_URL = "https://api.instantwebtools.net/v1/"