package com.MyApp.WeatherApp.ui.weather.future.list

import com.MyApp.WeatherApp.data.provider.UnitProvider
import com.MyApp.WeatherApp.data.repository.ForecastRepository
import com.MyApp.WeatherApp.internal.lazyDeferred
import com.MyApp.WeatherApp.ui.base.WeatherViewModel
import org.threeten.bp.LocalDate

class FutureListWeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider
) : WeatherViewModel(forecastRepository, unitProvider) {

    val weatherEntries by lazyDeferred {
        forecastRepository.getFutureWeatherList(LocalDate.now(), super.isMetricUnit)
    }
}
