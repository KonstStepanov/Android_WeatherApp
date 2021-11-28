package com.MyApp.WeatherApp.data.network.response

import com.google.gson.annotations.SerializedName
import com.MyApp.WeatherApp.data.db.entity.CurrentWeatherEntry
import com.MyApp.WeatherApp.data.db.entity.WeatherLocation

data class CurrentWeatherResponse(
    val location: WeatherLocation,
    @SerializedName("current")
    val currentWeatherEntry: CurrentWeatherEntry
)