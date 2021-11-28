package com.MyApp.WeatherApp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.MyApp.WeatherApp.data.db.entity.CURRENT_WEATHER_ID
import com.MyApp.WeatherApp.data.db.entity.CurrentWeatherEntry
import com.MyApp.WeatherApp.data.db.unitlocalized.current.MetricCurrentWeatherEntry


@Dao
interface CurrentWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(weatherEntry: CurrentWeatherEntry)

    @Query("select * from current_weather where id = $CURRENT_WEATHER_ID")
    fun getWeatherMetric(): LiveData<MetricCurrentWeatherEntry>
}