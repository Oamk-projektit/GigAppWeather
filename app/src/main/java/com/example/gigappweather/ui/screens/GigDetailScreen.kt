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
import com.example.gigappweather.ui.components.ErrorView
import com.example.gigappweather.ui.components.LoadingView
import com.example.gigappweather.ui.model.GigDetailUiModel
import com.example.gigappweather.ui.viewmodel.GigDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GigDetailScreen(
    viewModel: GigDetailViewModel,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.gig_detail_title)) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text(text = stringResource(id = R.string.back)) }
                },
            )
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
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = item.title, style = MaterialTheme.typography.titleLarge)
        Text(text = stringResource(id = R.string.detail_meta, item.dateIso, item.city))

        val weather = item.weather
        if (weather == null) {
            val msgRes = item.infoTextRes ?: R.string.no_forecast
            Text(text = stringResource(id = msgRes))
            return
        }

        Text(text = stringResource(id = R.string.weather_title), style = MaterialTheme.typography.titleMedium)
        Text(text = stringResource(id = R.string.temp_min_value, weather.tempMinC))
        Text(text = stringResource(id = R.string.temp_max_value, weather.tempMaxC))
        Text(text = stringResource(id = R.string.precip_value, weather.precipitationSumMm))
        Text(text = stringResource(id = R.string.wind_value, weather.windSpeedMax))

        val breakdown = item.scoreBreakdown
        if (breakdown != null) {
            Text(text = stringResource(id = R.string.score_value, breakdown.score.toString()))
            val recRes = recommendationTextRes(breakdown.score)
            Text(text = stringResource(id = R.string.recommendation_value, stringResource(id = recRes)))

            Text(text = stringResource(id = R.string.score_breakdown_title), style = MaterialTheme.typography.titleMedium)
            Text(text = stringResource(id = R.string.penalty_temp_value, breakdown.tempPenalty))
            Text(text = stringResource(id = R.string.penalty_precip_value, breakdown.precipitationPenalty))
            Text(text = stringResource(id = R.string.penalty_wind_value, breakdown.windPenalty))
        }
    }
}

private fun recommendationTextRes(score: Int): Int {
    return when {
        score >= 80 -> R.string.recommendation_great
        score >= 50 -> R.string.recommendation_ok
        else -> R.string.recommendation_bad
    }
}
