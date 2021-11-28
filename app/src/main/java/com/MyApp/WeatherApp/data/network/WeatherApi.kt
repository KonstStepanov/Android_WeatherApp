package com.MyApp.WeatherApp.data.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.MyApp.WeatherApp.data.network.response.CurrentWeatherResponse
import com.MyApp.WeatherApp.data.network.response.FutureWeatherResponse
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


const val API_KEY = "bf1db6b152d14d6088744201212811"

//https://api.weatherapi.com/v1/current.json?key=bf1db6b152d14d6088744201212811&q=London&lang=en

interface WeatherApiService {

    @GET("current.json")
    fun getCurrentWeather(
        @Query("q") location: String,
        @Query("lang") languageCode: String = "ru"
    ): Deferred<CurrentWeatherResponse>


    // https://api.weatherapi.com/v1/forecast.json?key=bf1db6b152d14d6088744201212811&q=Los%20Angeles&days=7
    @GET("forecast.json")
    fun getFutureWeather(
        @Query("q") location: String,
        @Query("days") days: Int,
        @Query("lang") languageCode: String = "ru"
    ): Deferred<FutureWeatherResponse>

    companion object {
        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor
        ): WeatherApiService {
            val requestInterceptor = Interceptor { chain ->

                val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("key", API_KEY)
                    .build()
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()

                return@Interceptor chain.proceed(request)
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .addInterceptor(connectivityInterceptor)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://api.weatherapi.com/v1/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApiService::class.java)
        }
    }
}