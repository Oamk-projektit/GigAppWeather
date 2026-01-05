package com.example.gigappweather.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gigappweather.R
import com.example.gigappweather.core.AppError
import com.example.gigappweather.core.Outcome
import com.example.gigappweather.core.UiState
import com.example.gigappweather.domain.logic.WeatherScoring
import com.example.gigappweather.domain.model.Gig
import com.example.gigappweather.domain.repository.GigRepository
import com.example.gigappweather.domain.repository.WeatherRepository
import com.example.gigappweather.ui.model.GigCardUiModel
import com.example.gigappweather.ui.model.GigWeatherStatus
import com.example.gigappweather.ui.model.WeatherDaySummaryUiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GigListViewModel(
    private val gigRepository: GigRepository,
    private val weatherRepository: WeatherRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val cityCache = mutableMapOf<String, Outcome<com.example.gigappweather.domain.model.WeatherSummary>>()
    private val inFlightCities = mutableSetOf<String>()
    private var latestGigs: List<Gig> = emptyList()

    private val _state = MutableStateFlow<UiState<List<GigCardUiModel>>>(UiState.Loading)
    val state: StateFlow<UiState<List<GigCardUiModel>>> = _state.asStateFlow()

    init {
        observeGigs()
    }

    private fun observeGigs() {
        viewModelScope.launch(ioDispatcher) {
            gigRepository.getAll().collectLatest { gigs ->
                latestGigs = gigs
                if (gigs.isEmpty()) {
                    _state.value = UiState.Empty
                    return@collectLatest
                }

                // Emit immediately; weather is fetched in background.
                _state.value = UiState.Success(mapToUi(gigs))
                ensureWeatherFetched(gigs)
            }
        }
    }

    private fun ensureWeatherFetched(gigs: List<Gig>) {
        gigs
            .map { it.city.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .forEach { city ->
                if (cityCache.containsKey(city) || inFlightCities.contains(city)) return@forEach
                inFlightCities.add(city)

                viewModelScope.launch(ioDispatcher) {
                    val outcome = weatherRepository.getDailyForecast(city)

                    when (outcome) {
                        is Outcome.Success -> {
                            cityCache[city] = outcome
                        }
                        is Outcome.Error -> {
                            when (outcome.error) {
                                AppError.CityNotFound,
                                AppError.NoForecastData,
                                AppError.NotFound,
                                -> cityCache[city] = outcome

                                AppError.Network,
                                is AppError.Http,
                                AppError.Serialization,
                                is AppError.Unknown,
                                -> {
                                    _state.value = UiState.Error(mapErrorToMessageRes(outcome.error))
                                    inFlightCities.remove(city)
                                    return@launch
                                }
                            }
                        }
                    }

                    inFlightCities.remove(city)

                    val current = latestGigs
                    if (current.isNotEmpty() && _state.value !is UiState.Error) {
                        _state.value = UiState.Success(mapToUi(current))
                    }
                }
            }
    }

    private fun mapToUi(gigs: List<Gig>): List<GigCardUiModel> {
        return gigs.map { gig ->
            val city = gig.city.trim()
            val cached = cityCache[city]

            when (cached) {
                null -> {
                    GigCardUiModel(
                        id = gig.id,
                        title = gig.title,
                        dateIso = gig.dateIso,
                        city = gig.city,
                        score = null,
                        summary = null,
                        weatherStatus = GigWeatherStatus.Loading,
                    )
                }

                is Outcome.Error -> {
                    val status = when (cached.error) {
                        AppError.CityNotFound -> GigWeatherStatus.CityNotFound
                        AppError.NoForecastData,
                        AppError.NotFound,
                        -> GigWeatherStatus.ForecastNotAvailableYet

                        else -> GigWeatherStatus.ForecastNotAvailableYet
                    }

                    GigCardUiModel(
                        id = gig.id,
                        title = gig.title,
                        dateIso = gig.dateIso,
                        city = gig.city,
                        score = null,
                        summary = null,
                        weatherStatus = status,
                    )
                }

                is Outcome.Success -> {
                    val day = cached.data.daily.firstOrNull { it.dateIso == gig.dateIso }
                    if (day == null) {
                        GigCardUiModel(
                            id = gig.id,
                            title = gig.title,
                            dateIso = gig.dateIso,
                            city = gig.city,
                            score = null,
                            summary = null,
                            weatherStatus = GigWeatherStatus.ForecastNotAvailableYet,
                        )
                    } else {
                        val summary = WeatherDaySummaryUiModel(
                            tempMinC = day.tempMinC,
                            tempMaxC = day.tempMaxC,
                            precipitationSumMm = day.precipitationSumMm,
                            windSpeedMax = day.windSpeedMax,
                        )
                        val score = WeatherScoring.computeWeatherScore(
                            isOutdoor = gig.isOutdoor,
                            tempMax = summary.tempMaxC,
                            precipitationSum = summary.precipitationSumMm,
                            windMax = summary.windSpeedMax,
                        )

                        GigCardUiModel(
                            id = gig.id,
                            title = gig.title,
                            dateIso = gig.dateIso,
                            city = gig.city,
                            score = score,
                            summary = summary,
                            weatherStatus = GigWeatherStatus.Available,
                        )
                    }
                }
            }
        }
    }

    private fun mapErrorToMessageRes(error: AppError): Int {
        return when (error) {
            AppError.Network -> R.string.error_network
            is AppError.Http -> R.string.error_api
            AppError.Serialization -> R.string.error_api
            is AppError.Unknown -> R.string.error_api
            AppError.CityNotFound -> R.string.error_city_not_found
            AppError.NoForecastData,
            AppError.NotFound,
            -> R.string.no_forecast
        }
    }

    suspend fun deleteGig(id: Long) {
        try {
            gigRepository.deleteById(id)
        } catch (_: Exception) {
            _state.value = UiState.Error(messageRes = R.string.error_db)
        }
    }
}
