package com.MyApp.WeatherApp.data.provider

import com.MyApp.WeatherApp.internal.UnitSystem


interface UnitProvider {
    fun getUnitSystem(): UnitSystem
}