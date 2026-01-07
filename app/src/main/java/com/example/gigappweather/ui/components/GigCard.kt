package com.example.gigappweather.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.gigappweather.R
import com.example.gigappweather.domain.logic.WeatherScoring
import com.example.gigappweather.domain.model.FinnishCities
import com.example.gigappweather.ui.model.GigCardUiModel
import com.example.gigappweather.ui.model.GigWeatherStatus

@Composable
fun GigCard(
    item: GigCardUiModel,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    isOnline: Boolean,
    onRetryWeather: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.title, style = MaterialTheme.typography.titleMedium)
                Text(text = item.dateIso, style = MaterialTheme.typography.bodyMedium)

                val cityName = FinnishCities.byId(item.cityId)?.let { stringResource(id = it.displayNameRes) } ?: item.cityId
                Text(text = cityName, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(id = R.string.weather_on_gig_day),
                    style = MaterialTheme.typography.titleSmall,
                )

                when (val status = item.weatherStatus) {
                    GigWeatherStatus.Loading -> {
                        SectionBox {
                            Text(text = stringResource(id = R.string.fetching_weather), style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    GigWeatherStatus.CityNotFound -> {
                        SectionBox {
                            Text(text = stringResource(id = R.string.error_city_not_found), style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    GigWeatherStatus.ForecastNotAvailableYet -> {
                        SectionBox {
                            Text(text = stringResource(id = R.string.forecast_not_available_yet), style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    is GigWeatherStatus.Error -> {
                        SectionBox {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = stringResource(id = status.messageRes),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f),
                                )
                                TextButton(
                                    onClick = onRetryWeather,
                                    enabled = isOnline,
                                ) {
                                    Text(text = stringResource(id = if (isOnline) R.string.retry else R.string.offline_badge_offline))
                                }
                            }
                        }
                    }

                    GigWeatherStatus.Available -> {
                        val weather = item.summary
                        val score = item.score

                        if (weather == null || score == null) {
                            SectionBox {
                                Text(text = stringResource(id = R.string.no_forecast), style = MaterialTheme.typography.bodyMedium)
                            }
                        } else {
                            SectionBox {
                                if (!isOnline) {
                                    Text(text = stringResource(id = R.string.error_network), style = MaterialTheme.typography.bodyMedium)
                                    Text(text = stringResource(id = R.string.offline_weather_cached), style = MaterialTheme.typography.bodyMedium)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                Text(text = stringResource(id = R.string.temp_min_value, weather.tempMinC))
                                Text(text = stringResource(id = R.string.temp_max_value, weather.tempMaxC))
                                Text(text = stringResource(id = R.string.precip_value, weather.precipitationSumMm))
                                Text(text = stringResource(id = R.string.wind_value, weather.windSpeedMax))

                                val recRes = WeatherScoring.recommendationTextRes(score)
                                Text(text = stringResource(id = R.string.score_value, score.toString()))
                                Text(text = stringResource(id = R.string.recommendation_value, stringResource(id = recRes)))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            TextButton(
                onClick = onDelete,
                modifier = Modifier.wrapContentWidth(Alignment.End),
                contentPadding = ButtonDefaults.TextButtonContentPadding,
            ) {
                Text(
                    text = stringResource(id = R.string.delete),
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                )
            }
        }
    }
}

@Composable
private fun SectionBox(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
    ) {
        Column(modifier = Modifier.padding(10.dp), content = content)
    }
}
