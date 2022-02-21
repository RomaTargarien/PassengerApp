package com.example.passengerapp

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@JvmField
val appModule = module {

    single<HelloRepository> { HelloRepositoryImpl() }

    viewModel { MainActivityViewModel(get()) }
}