package com.MyApp.WeatherApp.ui.weather.future.detail

import com.MyApp.WeatherApp.data.provider.UnitProvider
import com.MyApp.WeatherApp.data.repository.ForecastRepository
import com.MyApp.WeatherApp.internal.lazyDeferred
import com.MyApp.WeatherApp.ui.base.WeatherViewModel
import org.threeten.bp.LocalDate

class FutureDetailWeatherViewModel(
    private val detailDate: LocalDate,
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider
) : WeatherViewModel(forecastRepository, unitProvider) {

    val weather by lazyDeferred {
        forecastRepository.getFutureWeatherByDate(detailDate, super.isMetricUnit)
    }
}
