package com.MyApp.WeatherApp.data.repository

import androidx.lifecycle.LiveData
import com.MyApp.WeatherApp.data.db.CurrentWeatherDao
import com.MyApp.WeatherApp.data.db.FutureWeatherDao
import com.MyApp.WeatherApp.data.db.WeatherLocationDao
import com.MyApp.WeatherApp.data.db.entity.WeatherLocation
import com.MyApp.WeatherApp.data.db.unitlocalized.current.UnitSpecificCurrentWeatherEntry
import com.MyApp.WeatherApp.data.db.unitlocalized.future.detail.MetricDetailFutureWeatherEntry
import com.MyApp.WeatherApp.data.db.unitlocalized.future.detail.UnitSpecificDetailFutureWeatherEntry
import com.MyApp.WeatherApp.data.db.unitlocalized.future.list.MetricSimpleFutureWeatherEntry
import com.MyApp.WeatherApp.data.db.unitlocalized.future.list.UnitSpecificSimpleFutureWeatherEntry
import com.MyApp.WeatherApp.data.network.FORECAST_DAYS_COUNT
import com.MyApp.WeatherApp.data.network.WeatherNetworkDataSource
import com.MyApp.WeatherApp.data.network.response.CurrentWeatherResponse
import com.MyApp.WeatherApp.data.network.response.FutureWeatherResponse
import com.MyApp.WeatherApp.data.provider.LocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime
import java.util.*

class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val futureWeatherDao: FutureWeatherDao,
    private val weatherLocationDao: WeatherLocationDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
    private val locationProvider: LocationProvider
    ) : ForecastRepository {

    init {
        weatherNetworkDataSource.apply {
            downloadedCurrentWeather.observeForever { newCurrentWeather ->
                persistFetchedCurrentWeather(newCurrentWeather)
            }
            downloadedFutureWeather.observeForever { newFutureWeather ->
                persistFetchedFutureWeather(newFutureWeather)
            }
        }
    }

    override suspend fun getCurrentWeather(metric: Boolean): LiveData<out UnitSpecificCurrentWeatherEntry> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext currentWeatherDao.getWeatherMetric()
        }
    }

    override suspend fun getFutureWeatherList(
        startDate: LocalDate,
        metric: Boolean
    ): LiveData<List<MetricSimpleFutureWeatherEntry>> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext futureWeatherDao.getSimpleWeatherForecastsMetric(startDate)
        }
    }

    override suspend fun getFutureWeatherByDate(
        date: LocalDate,
        metric: Boolean
    ): LiveData<MetricDetailFutureWeatherEntry> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext futureWeatherDao.getDetailedWeatherByDateMetric(date)
        }
    }

    override suspend fun getWeatherLocation(): LiveData<WeatherLocation> {
        return withContext(Dispatchers.IO) {
            return@withContext weatherLocationDao.getLocation()
        }
    }

    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherDao.upsert(fetchedWeather.currentWeatherEntry)
            weatherLocationDao.upsert(fetchedWeather.location)
        }
    }

    private fun persistFetchedFutureWeather(fetchedWeather: FutureWeatherResponse) {

        fun deleteOldForecastData() {
            val today = LocalDate.now()
            futureWeatherDao.deleteOldEntries(today)
        }

        GlobalScope.launch(Dispatchers.IO) {
            deleteOldForecastData()
            val futureWeatherList = fetchedWeather.futureWeatherEntries.entries
            futureWeatherDao.insert(futureWeatherList)
            weatherLocationDao.upsert(fetchedWeather.location)
        }
    }

    private suspend fun initWeatherData() {
        val lastWeatherLocation = weatherLocationDao.getLocationNonLive()

        if (lastWeatherLocation == null
            || locationProvider.hasLocationChanged(lastWeatherLocation)) {
            fetchCurrentWeather()
            fetchFutureWeather()
            return
        }

        if (isFetchCurrentNeeded(lastWeatherLocation.zonedDateTime))
            fetchCurrentWeather()

        if (isFetchFutureNeeded())
            fetchFutureWeather()
    }

    private suspend fun fetchCurrentWeather() {
        weatherNetworkDataSource.fetchCurrentWeather(
            locationProvider.getPreferredLocationString(),
            Locale.getDefault().language
        )
    }

    private suspend fun fetchFutureWeather() {
        weatherNetworkDataSource.fetchFutureWeather(
            locationProvider.getPreferredLocationString(),
            Locale.getDefault().language
        )
    }

    private fun isFetchCurrentNeeded(lastFetchTime: ZonedDateTime): Boolean {
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }

    private fun isFetchFutureNeeded(): Boolean {
        val today = LocalDate.now()
        val futureWeatherCount = futureWeatherDao.countFutureWeather(today)
        return futureWeatherCount < FORECAST_DAYS_COUNT
    }
}