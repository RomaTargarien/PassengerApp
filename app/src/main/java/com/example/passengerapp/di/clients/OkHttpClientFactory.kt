package com.example.passengerapp.di.clients

import android.content.Context
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class OkHttpClientFactory {

    fun createClient(context: Context): OkHttpClient {
        val cache = Cache(context.cacheDir, CACHE_SIZE)
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(getLoggingInterceptor())
//            .addNetworkInterceptor(getNetworkInterceptor())
//            .cache(cache)
            .build()
    }

    private fun getLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }


    private fun getNetworkInterceptor(): Interceptor =
        Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val maxAge = MAX_AGE_IN_SECONDS
            response.newBuilder()
                .header("Cache-Control", "public, max-age=$maxAge")
                .build()
        }

    companion object {
        private const val CACHE_SIZE = 10 * 1024 * 1024L
        private const val MAX_AGE_IN_SECONDS = 60 * 60
    }
}