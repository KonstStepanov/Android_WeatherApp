package com.MyApp.WeatherApp.ui.weather.current

import com.MyApp.WeatherApp.data.provider.UnitProvider
import com.MyApp.WeatherApp.data.repository.ForecastRepository
import com.MyApp.WeatherApp.internal.lazyDeferred
import com.MyApp.WeatherApp.ui.base.WeatherViewModel

class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider
) : WeatherViewModel(forecastRepository, unitProvider) {

    val weather by lazyDeferred {
        forecastRepository.getCurrentWeather(super.isMetricUnit)
    }
}
