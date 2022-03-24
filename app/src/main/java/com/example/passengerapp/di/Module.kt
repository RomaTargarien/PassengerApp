package com.example.passengerapp

import android.content.Context
import com.example.passengerapp.di.clients.OkHttpClientFactory
import com.example.passengerapp.network.PassengerApi
import com.example.passengerapp.repository.PassengerRepository
import com.example.passengerapp.repository.PassengerRepositoryImpl
import com.example.passengerapp.ui.base.TextInputValidator
import com.example.passengerapp.ui.base.TextInputValidatorImpl
import com.example.passengerapp.ui.screens.MainActivityViewModel
import com.example.passengerapp.ui.screens.passengercreating.PassengerCreatingViewModel
import com.example.passengerapp.ui.screens.passengerlist.PassengerListViewModel
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@FlowPreview
@JvmField
val appModule = module {

    single { provideRetrofitInstance(context = androidContext(), okHttpClientFactory = get()) }

    single { provideNetworkApi(get()) }

    single<PassengerRepository> { PassengerRepositoryImpl(get()) }

    single<TextInputValidator> { TextInputValidatorImpl(context = androidContext()) }

    factory { OkHttpClientFactory() }


    viewModel { MainActivityViewModel() }

    viewModel { PassengerListViewModel(get()) }

    viewModel { PassengerCreatingViewModel(get(), get()) }

}

fun provideRetrofitInstance(context: Context, okHttpClientFactory: OkHttpClientFactory): Retrofit =
    Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(getGson()))
        .client(okHttpClientFactory.createClient(context))
        .build()

fun provideNetworkApi(retrofit: Retrofit): PassengerApi =
    retrofit.create(PassengerApi::class.java)

fun getGson(): Gson = GsonBuilder().addDeserializationExclusionStrategy(object : ExclusionStrategy {
    override fun shouldSkipField(f: FieldAttributes?): Boolean = false

    override fun shouldSkipClass(clazz: Class<*>?): Boolean = false

}).create()

private const val BASE_URL = "https://api.instantwebtools.net/v1/"