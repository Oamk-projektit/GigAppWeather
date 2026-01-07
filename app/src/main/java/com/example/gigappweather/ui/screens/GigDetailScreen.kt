package com.example.gigappweather.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gigappweather.R
import com.example.gigappweather.core.UiState
import com.example.gigappweather.domain.logic.WeatherScoring
import com.example.gigappweather.domain.model.FinnishCities
import com.example.gigappweather.ui.components.ErrorView
import com.example.gigappweather.ui.components.LoadingView
import com.example.gigappweather.ui.components.OfflineBanner
import com.example.gigappweather.ui.model.GigDetailUiModel
import com.example.gigappweather.ui.viewmodel.AppViewModel
import com.example.gigappweather.ui.viewmodel.GigDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GigDetailScreen(
    viewModel: GigDetailViewModel,
    appViewModel: AppViewModel,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val isOnline by appViewModel.isOnline.collectAsState()
    val simulatedOffline by appViewModel.simulatedOffline.collectAsState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.gig_detail_title)) },
                    navigationIcon = {
                        TextButton(onClick = onBack) { Text(text = stringResource(id = R.string.back)) }
                    },
                )
                if (!isOnline) {
                    OfflineBanner(isSimulated = simulatedOffline)
                }
            }
        },
        modifier = modifier,
    ) { padding ->
        when (val s = state) {
            UiState.Loading -> LoadingView(modifier = Modifier.padding(padding))
            UiState.Empty -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(text = stringResource(id = R.string.empty_detail))
                }
            }
            is UiState.Error -> ErrorView(
                messageRes = s.messageRes,
                onRetry = onRetry,
                modifier = Modifier.padding(padding),
            )
            is UiState.Success -> DetailContent(
                item = s.data,
                isOnline = isOnline,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
            )
        }
    }
}

@Composable
private fun DetailContent(
    item: GigDetailUiModel,
    isOnline: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = item.title, style = MaterialTheme.typography.titleLarge)
        val cityName = FinnishCities.byId(item.cityId)?.let { stringResource(id = it.displayNameRes) } ?: item.cityId
        Text(text = stringResource(id = R.string.detail_meta, item.dateIso, cityName))

        val weather = item.weather
        if (weather == null) {
            val msgRes = item.infoTextRes ?: R.string.no_forecast
            Text(text = stringResource(id = msgRes))
            return
        }

        Text(text = stringResource(id = R.string.weather_title), style = MaterialTheme.typography.titleMedium)
        if (!isOnline) {
            Text(text = stringResource(id = R.string.error_network), style = MaterialTheme.typography.bodyMedium)
            Text(text = stringResource(id = R.string.offline_weather_cached), style = MaterialTheme.typography.bodyMedium)
        }
        Text(text = stringResource(id = R.string.temp_min_value, weather.tempMinC))
        Text(text = stringResource(id = R.string.temp_max_value, weather.tempMaxC))
        Text(text = stringResource(id = R.string.precip_value, weather.precipitationSumMm))
        Text(text = stringResource(id = R.string.wind_value, weather.windSpeedMax))

        val breakdown = item.scoreBreakdown
        if (breakdown != null) {
            Text(text = stringResource(id = R.string.score_value, breakdown.score.toString()))
            val recRes = WeatherScoring.recommendationTextRes(breakdown.score)
            Text(text = stringResource(id = R.string.recommendation_value, stringResource(id = recRes)))

            Text(text = stringResource(id = R.string.score_breakdown_title), style = MaterialTheme.typography.titleMedium)
            Text(text = stringResource(id = R.string.penalty_temp_value, breakdown.tempPenalty))
            Text(text = stringResource(id = R.string.penalty_precip_value, breakdown.precipitationPenalty))
            Text(text = stringResource(id = R.string.penalty_wind_value, breakdown.windPenalty))
        }
    }
}
