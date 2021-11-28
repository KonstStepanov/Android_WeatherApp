package com.MyApp.WeatherApp.data.network.response

import com.google.gson.annotations.SerializedName
import com.MyApp.WeatherApp.data.db.entity.FutureWeatherEntry

data class ForecastDaysContainer(
    @SerializedName("forecastday")
    val entries: List<FutureWeatherEntry>
)