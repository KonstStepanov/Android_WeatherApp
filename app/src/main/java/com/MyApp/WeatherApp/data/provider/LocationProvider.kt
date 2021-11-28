package com.MyApp.WeatherApp.data.provider

import com.MyApp.WeatherApp.data.db.entity.WeatherLocation


interface LocationProvider {
    suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean
    suspend fun getPreferredLocationString(): String
}