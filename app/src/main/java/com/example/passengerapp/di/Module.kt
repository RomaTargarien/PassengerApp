package com.example.passengerapp

import com.example.passengerapp.network.PassengerApi
import com.example.passengerapp.repository.PassengerRepository
import com.example.passengerapp.repository.PassengerRepositoryImpl
import com.example.passengerapp.ui.base.Validation
import com.example.passengerapp.ui.base.ValidationImpl
import com.example.passengerapp.ui.screens.MainActivityViewModel
import com.example.passengerapp.ui.screens.passengercreating.PassengerCreatingViewModel
import com.example.passengerapp.ui.screens.passengerlist.PassengerListViewModel
import kotlinx.coroutines.FlowPreview
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@FlowPreview
@JvmField
val appModule = module {

    factory { provideRetrofitInstance() }

    factory { provideNetworkApi(get()) }

    single<PassengerRepository> { PassengerRepositoryImpl(get()) }

    single<Validation> { ValidationImpl(context = androidContext()) }

    viewModel { MainActivityViewModel() }

    viewModel { PassengerListViewModel(get()) }

    viewModel {PassengerCreatingViewModel(get(),get())}

}


private fun provideLoggingInterceptor(): OkHttpClient {
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.HEADERS
    return OkHttpClient.Builder().addInterceptor(logging).build()
}

fun provideRetrofitInstance(): Retrofit =
    Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(provideLoggingInterceptor())
        .build()

fun provideNetworkApi(retrofit: Retrofit): PassengerApi =
    retrofit.create(PassengerApi::class.java)

private const val BASE_URL = "https://api.instantwebtools.net/v1/"