package com.example.passengerapp.di.clients

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class RetrofitClient {
    companion object {

        fun provideLoggingClient(): OkHttpClient {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.HEADERS
            val client = OkHttpClient.Builder().addInterceptor(logging).build()
            return client
        }

        fun provideNetworkClient(context: Context): OkHttpClient {
            val cacheSize = (10 * 1024 * 1024).toLong()
            val cache = Cache(context.cacheDir, cacheSize)

            val onlineInterceptor = object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val response = chain.proceed(chain.request())
                    val maxAge = 60
                    return response.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .removeHeader("Pragma")
                        .build()
                }
            }

            val offlineInterceptor = object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    var request = chain.request()
                    if (!isInternetAvailable(context)) {
                        val maxStale = 60 * 60 * 24 // Offline cache available for 1 day
                        request = request.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                            .removeHeader("Pragma")
                            .build()
                    }
                    return chain.proceed(request)
                }
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(offlineInterceptor)
                .addNetworkInterceptor(onlineInterceptor)
                .cache(cache)
                .build()

            return client
        }

        fun provideTimeoutClient(): OkHttpClient {
            val client = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
            return client
        }

        private fun isInternetAvailable(context: Context): Boolean {
            var isConnected = false
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            if (activeNetwork != null && activeNetwork.isConnected)
                isConnected = true
            return isConnected
        }
    }
}