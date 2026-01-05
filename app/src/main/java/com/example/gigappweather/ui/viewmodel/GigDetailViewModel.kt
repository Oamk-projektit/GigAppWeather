package com.example.gigappweather.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gigappweather.R
import com.example.gigappweather.core.AppError
import com.example.gigappweather.core.Outcome
import com.example.gigappweather.core.UiState
import com.example.gigappweather.domain.logic.WeatherScoring
import com.example.gigappweather.domain.model.FinnishCities
import com.example.gigappweather.domain.repository.GigRepository
import com.example.gigappweather.domain.repository.WeatherRepository
import com.example.gigappweather.ui.model.GigDetailUiModel
import com.example.gigappweather.ui.model.WeatherDaySummaryUiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GigDetailViewModel(
    private val gigId: Long,
    private val gigRepository: GigRepository,
    private val weatherRepository: WeatherRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<GigDetailUiModel>>(UiState.Loading)
    val state: StateFlow<UiState<GigDetailUiModel>> = _state.asStateFlow()

    init {
        observeGig()
    }

    private fun observeGig() {
        viewModelScope.launch(ioDispatcher) {
            gigRepository.getAll().collectLatest { gigs ->
                val gig = gigs.firstOrNull { it.id == gigId }
                if (gig == null) {
                    _state.value = UiState.Empty
                    return@collectLatest
                }

                _state.value = UiState.Loading

                val city = FinnishCities.byId(gig.cityId)
                val weatherOutcome = if (city == null) {
                    Outcome.Error(AppError.CityNotFound)
                } else {
                    weatherRepository.getDailyForecast(
                        latitude = city.latitude,
                        longitude = city.longitude,
                    )
                }

                val weatherDay = when (weatherOutcome) {
                    is Outcome.Success -> weatherOutcome.data.daily.firstOrNull { it.dateIso == gig.dateIso }
                    is Outcome.Error -> null
                }

                if (weatherOutcome is Outcome.Error) {
                    when (weatherOutcome.error) {
                        AppError.Network -> {
                            _state.value = UiState.Error(R.string.error_network)
                            return@collectLatest
                        }
                        is AppError.Http,
                        AppError.Serialization,
                        is AppError.Unknown -> {
                            _state.value = UiState.Error(R.string.error_api)
                            return@collectLatest
                        }
                        else -> Unit
                    }
                }

                val weatherSummary = weatherDay?.let {
                    WeatherDaySummaryUiModel(
                        tempMinC = it.tempMinC,
                        tempMaxC = it.tempMaxC,
                        precipitationSumMm = it.precipitationSumMm,
                        windSpeedMax = it.windSpeedMax,
                    )
                }

                val breakdown = weatherDay?.let {
                    WeatherScoring.computeScoreBreakdown(
                        isOutdoor = gig.isOutdoor,
                        tempMax = it.tempMaxC,
                        precipitationSum = it.precipitationSumMm,
                        windMax = it.windSpeedMax,
                    )
                }

                val infoRes = when (weatherOutcome) {
                    is Outcome.Error -> when (weatherOutcome.error) {
                        AppError.CityNotFound -> R.string.error_city_not_found
                        AppError.NoForecastData,
                        AppError.NotFound -> R.string.forecast_not_available_yet
                        else -> null
                    }
                    is Outcome.Success -> if (weatherDay == null) R.string.forecast_not_available_yet else null
                }

                _state.value = UiState.Success(
                    GigDetailUiModel(
                        id = gig.id,
                        title = gig.title,
                        dateIso = gig.dateIso,
                        cityId = gig.cityId,
                        isOutdoor = gig.isOutdoor,
                        createdAt = gig.createdAt,
                        weather = weatherSummary,
                        scoreBreakdown = breakdown,
                        infoTextRes = infoRes,
                    )
                )
            }
        }
    }

    fun retry() = Unit
}
