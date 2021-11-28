package com.MyApp.WeatherApp.ui.base

import androidx.lifecycle.ViewModel
import com.MyApp.WeatherApp.data.provider.UnitProvider
import com.MyApp.WeatherApp.data.repository.ForecastRepository
import com.MyApp.WeatherApp.internal.UnitSystem
import com.MyApp.WeatherApp.internal.lazyDeferred


abstract class WeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider
) : ViewModel() {

    private val unitSystem = unitProvider.getUnitSystem()

    val isMetricUnit: Boolean
        get() = unitSystem == UnitSystem.METRIC

    val weatherLocation by lazyDeferred {
        forecastRepository.getWeatherLocation()
    }
}