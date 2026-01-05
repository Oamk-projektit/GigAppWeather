package com.example.gigappweather.data.repository

import com.example.gigappweather.core.AppError
import com.example.gigappweather.core.Outcome
import com.example.gigappweather.data.remote.RetrofitProvider
import com.example.gigappweather.data.remote.weather.WeatherService
import com.example.gigappweather.domain.model.DailyWeather
import com.example.gigappweather.domain.model.WeatherSummary
import com.example.gigappweather.domain.repository.WeatherRepository
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

class WeatherRepositoryImpl(
    private val weatherService: WeatherService = RetrofitProvider
        .create(WeatherService.BASE_URL)
        .create(WeatherService::class.java),
) : WeatherRepository {

    override suspend fun getDailyForecast(latitude: Double, longitude: Double): Outcome<WeatherSummary> {
        return try {
            val forecastResponse = weatherService.getDailyForecast(
                latitude = latitude,
                longitude = longitude,
            )

            val daily = forecastResponse.daily
                ?: return Outcome.Error(AppError.Serialization)

            val size = listOf(
                daily.time.size,
                daily.temperature2mMin.size,
                daily.temperature2mMax.size,
                daily.precipitationSum.size,
                daily.windSpeed10mMax.size,
            ).minOrNull() ?: 0

            if (size == 0) {
                return Outcome.Error(AppError.NoForecastData)
            }

            val days = (0 until size).map { index ->
                DailyWeather(
                    dateIso = daily.time[index],
                    tempMinC = daily.temperature2mMin[index],
                    tempMaxC = daily.temperature2mMax[index],
                    precipitationSumMm = daily.precipitationSum[index],
                    windSpeedMax = daily.windSpeed10mMax[index],
                )
            }

            Outcome.Success(
                WeatherSummary(
                    latitude = latitude,
                    longitude = longitude,
                    daily = days,
                )
            )
        } catch (e: IOException) {
            Outcome.Error(AppError.Network, e)
        } catch (e: HttpException) {
            Outcome.Error(AppError.Http(code = e.code(), message = e.message()), e)
        } catch (e: SerializationException) {
            Outcome.Error(AppError.Serialization, e)
        } catch (e: Exception) {
            Outcome.Error(AppError.Unknown(message = e.message), e)
        }
    }
}
