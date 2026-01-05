package com.example.gigappweather.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.gigappweather.R
import com.example.gigappweather.ui.model.GigCardUiModel
import com.example.gigappweather.ui.model.GigWeatherStatus

@Composable
fun GigCard(
    item: GigCardUiModel,
    onClick: () -> Unit,
    onDelete: () -> Unit,
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
                Text(text = item.city, style = MaterialTheme.typography.bodyMedium)

                val scoreText = when (item.weatherStatus) {
                    GigWeatherStatus.Loading -> stringResource(id = R.string.loading)
                    GigWeatherStatus.CityNotFound -> stringResource(id = R.string.error_city_not_found)
                    GigWeatherStatus.ForecastNotAvailableYet -> stringResource(id = R.string.forecast_not_available_yet)
                    GigWeatherStatus.Available -> item.score?.toString() ?: stringResource(id = R.string.no_forecast)
                }

                Text(
                    text = stringResource(id = R.string.score_value, scoreText),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
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
